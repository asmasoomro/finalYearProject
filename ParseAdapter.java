package com.example.fypapp;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fypapp.model.FeedbackEntry;

import java.util.ArrayList;

public class ParseAdapter extends RecyclerView.Adapter<ParseAdapter.ViewHolder> {

    private ArrayList<ParseItem> parseItems;
    private ArrayList<Float> positivePercentages;
    private Context context;
    private OnHeartIconClickListener onHeartIconClickListener;
    private OnFeedbackIconClickListener onFeedbackIconClickListener;

    public ParseAdapter(ArrayList<ParseItem> parseItems, ArrayList<Float> positivePercentages, Context context, OnHeartIconClickListener onHeartIconClickListener, OnFeedbackIconClickListener onFeedbackIconClickListener) {
        this.parseItems = parseItems;
        this.positivePercentages = positivePercentages;
        this.context = context;
        this.onHeartIconClickListener = onHeartIconClickListener;
        this.onFeedbackIconClickListener = onFeedbackIconClickListener;
    }
    public void setItems(ArrayList<ParseItem> newItems) {
        this.parseItems = newItems;
        notifyDataSetChanged(); // Notify adapter that data set has changed
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parse_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParseItem parseItem = parseItems.get(position);
        holder.textView.setText(parseItem.getTitle());

        // Display the positive percentage
        holder.positivePercentageTextView.setText("Positive: " + parseItem.getPositivePercentage() + "%");
        holder.checkmark.setVisibility(parseItem.isPositiveFeedback() ? View.VISIBLE : View.GONE);

        int heartIconResource = parseItem.isFavorited() ? R.drawable.heart2 : R.drawable.heart;
        holder.heartIcon.setImageResource(heartIconResource);

        holder.heartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean newFavoritedState = !parseItem.isFavorited();
                parseItem.setFavorited(newFavoritedState);
                notifyDataSetChanged();

                if (onHeartIconClickListener != null) {
                    onHeartIconClickListener.onHeartIconClick(parseItem.getTitle(), newFavoritedState);
                }
            }
        });

        holder.feedbackIcon.setOnClickListener(view -> {
            int clickedPosition = holder.getAdapterPosition();
            if (clickedPosition != RecyclerView.NO_POSITION && onFeedbackIconClickListener != null) {
                onFeedbackIconClickListener.onFeedbackIconClick(parseItems.get(clickedPosition).getTitle());
            }
        });

        int newPopularIconVisibility = parseItem.getFeedbackEntry() != null && parseItem.getFeedbackEntry().isPopular() ? View.VISIBLE : View.GONE;
        holder.popularIcon.setVisibility(newPopularIconVisibility);
    }

    @Override
    public int getItemCount() {
        return parseItems.size();
    }

    public void setItems(ArrayList<ParseItem> items, ArrayList<Float> positivePercentages) {
        // Update the positive percentage values along with the items
        this.parseItems = items;
        this.positivePercentages = positivePercentages;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TextView positivePercentageTextView;
        ImageView heartIcon;
        ImageView feedbackIcon;
        ImageView popularIcon;
        ImageView checkmark;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            positivePercentageTextView = itemView.findViewById(R.id.positivePercentageTextView);
            heartIcon = itemView.findViewById(R.id.heartIcon);
            feedbackIcon = itemView.findViewById(R.id.feedbackIcon);
            popularIcon = itemView.findViewById(R.id.popularIcon);
            checkmark = itemView.findViewById(R.id.imageViewTick);
        }
    }

    public interface OnHeartIconClickListener {
        void onHeartIconClick(String activity, boolean favorited);
    }

    public interface OnFeedbackIconClickListener {
        void onFeedbackIconClick(String activity);
    }
}


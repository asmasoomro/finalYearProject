package com.example.fypapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ParseAdapter extends RecyclerView.Adapter<ParseAdapter.ViewHolder> {

    private ArrayList<ParseItem> parseItems;
    private Context context;
    private OnHeartIconClickListener onHeartIconClickListener;
    private OnFeedbackIconClickListener onFeedbackIconClickListener; // Add this property

    public ParseAdapter(ArrayList<ParseItem> parseItems, Context context, OnHeartIconClickListener onHeartIconClickListener, OnFeedbackIconClickListener onFeedbackIconClickListener) {
        this.parseItems = parseItems;
        this.context = context;
        this.onHeartIconClickListener = onHeartIconClickListener;
        this.onFeedbackIconClickListener = onFeedbackIconClickListener;
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
    }

    @Override
    public int getItemCount() {
        return parseItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView heartIcon;
        ImageView feedbackIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            heartIcon = itemView.findViewById(R.id.heartIcon);
            feedbackIcon = itemView.findViewById(R.id.feedbackIcon);
        }
    }

    public interface OnHeartIconClickListener {
        void onHeartIconClick(String activity, boolean favorited);
    }

    public interface OnFeedbackIconClickListener {
        void onFeedbackIconClick(String activity);
    }
}


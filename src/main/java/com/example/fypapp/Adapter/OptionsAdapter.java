package com.example.fypapp.Adapter;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fypapp.Favorite;
import com.example.fypapp.Journal;
import com.example.fypapp.R;
import com.example.fypapp.SleepTracker1;
import com.example.fypapp.WeeklyMood;
import com.example.fypapp.model.OptionItem;

import java.util.List;

public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.ViewHolder> {

    private List<OptionItem> optionItemList;
    private Context context;

    public OptionsAdapter(Context context, List<OptionItem> optionItemList) {
        this.context = context;
        this.optionItemList = optionItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_option, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OptionItem optionItem = optionItemList.get(position);

        // Set the image and text for the current option
        holder.imageView.setImageResource(optionItem.getImageResource());
        holder.textView.setText(optionItem.getOptionName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleOptionClick(optionItem);
            }
        });
    }

    private void handleOptionClick(OptionItem optionItem) {
        Intent intent;
        switch (optionItem.getOptionName()) {
            case "Journal":
                intent = new Intent(context, Journal.class);
                break;
            case "Sleep Tracker":
                intent = new Intent(context, SleepTracker1.class);
                break;
            case "Favorite":
                intent = new Intent(context, Favorite.class);
                break;
            case "Weekly Mood Chart":
                intent = new Intent(context, WeeklyMood.class);
                break;
            default:
                return;
        }

        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return optionItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageOption);
            textView = itemView.findViewById(R.id.textOption);
        }
    }
}

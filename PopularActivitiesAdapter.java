package com.example.fypapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PopularActivitiesAdapter extends RecyclerView.Adapter<PopularActivitiesAdapter.ViewHolder> {

    private ArrayList<String> popularActivityTitles;

    public PopularActivitiesAdapter(ArrayList<String> popularActivityTitles) {
        this.popularActivityTitles = popularActivityTitles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_popular_activity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String title = popularActivityTitles.get(position);
        holder.bind(title);
    }

    @Override
    public int getItemCount() {
        return popularActivityTitles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
        }

        public void bind(String title) {
            titleTextView.setText(title);
        }
    }

    public void updateData(ArrayList<String> newPopularActivityTitles) {
        popularActivityTitles.clear();
        popularActivityTitles.addAll(newPopularActivityTitles);
        notifyDataSetChanged(); // Refresh the list
    }
}

package com.example.fypapp.Adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fypapp.R;
import com.example.fypapp.model.SleepData;

import java.util.List;

public class SleepDataAdapter extends RecyclerView.Adapter<SleepDataAdapter.ViewHolder> {

    private Context context;
    private List<SleepData> sleepDataList;

    public SleepDataAdapter(Context context, List<SleepData> sleepDataList) {
        this.context = context;
        this.sleepDataList = sleepDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sleep_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SleepData sleepData = sleepDataList.get(position);

        // Set the date and duration in the TextViews
        holder.dateTextView.setText("Date: " + sleepData.getDateOfSleep());
        holder.durationTextView.setText("Duration: " + sleepData.getDuration() + " minutes");
    }

    @Override
    public int getItemCount() {
        return sleepDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView durationTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            durationTextView = itemView.findViewById(R.id.durationTextView);
        }
    }
}

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

import com.example.fypapp.ActivitiesNotWork;
import com.example.fypapp.ActivitiesWorked;
import com.example.fypapp.Favorite;
import com.example.fypapp.Feedback;
import com.example.fypapp.Journal;
import com.example.fypapp.R;
import com.example.fypapp.SleepTracker1;
import com.example.fypapp.WeeklyMood;
import com.example.fypapp.model.OptionItem;

import java.util.List;

public class OptionsAdapter  {

    private List<OptionItem> optionItemList;
    private Context context;
    private LayoutInflater inflater;

    public OptionsAdapter(Context context, List<OptionItem> optionItemList) {
        this.context = context;
        this.optionItemList = optionItemList;
        this.inflater = LayoutInflater.from(context);
    }
    public View createItemView(ViewGroup parent, int position) {
        View view = inflater.inflate(R.layout.item_option, parent, false);
        OptionItem optionItem = optionItemList.get(position);

        ImageView imageView = view.findViewById(R.id.imageOption);
        TextView textView = view.findViewById(R.id.textOption);

        imageView.setImageResource(optionItem.getImageResource());
        textView.setText(optionItem.getOptionName());

        view.setOnClickListener(v -> handleOptionClick(optionItem));

        return view;
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
            case "Activities That Worked for me":
                intent = new Intent(context, ActivitiesWorked.class);
                break;
            case "Activities That Did not Work for me":
                intent = new Intent(context, ActivitiesNotWork.class);
                break;
            default:
                return;
        }

        context.startActivity(intent);
    }

}

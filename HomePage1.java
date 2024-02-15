package com.example.fypapp;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fypapp.Adapter.OptionsAdapter;
import com.example.fypapp.model.OptionItem;

import java.util.ArrayList;
import java.util.List;

public class HomePage1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page1);

        List<OptionItem> optionItemList = new ArrayList<>();
        optionItemList.add(new OptionItem(R.drawable.journal, "Journal"));
        optionItemList.add(new OptionItem(R.drawable.sleep, "Sleep Tracker"));
        optionItemList.add(new OptionItem(R.drawable.favorite, "Favorite"));
        optionItemList.add(new OptionItem(R.drawable.weekly, "Weekly Mood Chart"));
        optionItemList.add(new OptionItem(R.drawable.feedback, "Feedback"));

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        OptionsAdapter optionsAdapter = new OptionsAdapter(this, optionItemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(optionsAdapter);
    }
}

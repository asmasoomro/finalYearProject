package com.example.fypapp;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fypapp.Adapter.OptionsAdapter;
import com.example.fypapp.model.OptionItem;

import java.util.ArrayList;
import java.util.List;

public class HomePage1 extends AppCompatActivity {
    private GridLayout gridLayout;
    private OptionsAdapter optionsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page1);

        gridLayout = findViewById(R.id.gridLayout);

        List<OptionItem> optionItemList = new ArrayList<>();
        optionItemList.add(new OptionItem(R.drawable.journal, "Journal"));
        optionItemList.add(new OptionItem(R.drawable.sleep, "Sleep Tracker"));
        optionItemList.add(new OptionItem(R.drawable.favorite, "Favorite"));
        optionItemList.add(new OptionItem(R.drawable.week, "Weekly Mood Chart"));
        optionItemList.add(new OptionItem(R.drawable.tick, "Activities That Worked for me"));
        optionItemList.add(new OptionItem(R.drawable.x, "Activities That Did not Work for me"));
        optionsAdapter = new OptionsAdapter(this, optionItemList);

        populateGridLayout(optionItemList);
    }
    private void populateGridLayout(List<OptionItem> optionItems) {
        for (int i = 0; i < optionItems.size(); i++) {
            View itemView = optionsAdapter.createItemView(gridLayout, i);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0; // Use 0 for width to stretch items equally within the column
            params.height = GridLayout.LayoutParams.WRAP_CONTENT; // Height to wrap content
            params.setMargins(8, 8, 8, 8);
            params.columnSpec = GridLayout.spec(i % 2, 1f); // Column index, set weight to 1f
            params.rowSpec = GridLayout.spec(i / 2); // Row index

            itemView.setLayoutParams(params);

            gridLayout.addView(itemView);
        }
    }

}




package com.example.fypapp;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fypapp.Adapter.OptionsAdapter;
import com.example.fypapp.model.OptionItem;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class HomePage1 extends AppCompatActivity {
    private GridLayout gridLayout;
    private OptionsAdapter optionsAdapter;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page1);

        gridLayout = findViewById(R.id.gridLayout);
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call Firebase logout method
                FirebaseAuth.getInstance().signOut();

                startActivity(new Intent(HomePage1.this, MainActivity.class));
                finish();
            }
        });

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
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.setMargins(8, 8, 8, 8);
            params.columnSpec = GridLayout.spec(i % 2, 1f);
            params.rowSpec = GridLayout.spec(i / 2); // Row index

            itemView.setLayoutParams(params);

            gridLayout.addView(itemView);
        }
    }

}




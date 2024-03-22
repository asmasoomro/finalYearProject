package com.example.fypapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PopularActivities extends AppCompatActivity {

    private RecyclerView popularRecyclerView;
    private PopularActivitiesAdapter popularAdapter;
    private ArrayList<String> popularActivityTitles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_activities);

        //popularActivityTitles = getIntent().getStringArrayListExtra("popularActivityTitles");

        popularRecyclerView = findViewById(R.id.recyclerViewPopularActivities);
        popularRecyclerView.setHasFixedSize(true);
        popularRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Assuming you've passed the popularActivityTitles through Intent
      //  popularActivityTitles = getIntent().getStringArrayListExtra("popularActivityTitles");

        // Set up RecyclerView
        popularAdapter = new PopularActivitiesAdapter(popularActivityTitles);
        popularRecyclerView.setAdapter(popularAdapter);
        loadPopularActivities();
    }

    private void loadPopularActivities() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("user_feedback");
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> newPopularActivityTitles = new ArrayList<>();
                for (DataSnapshot feedbackSnapshot : dataSnapshot.getChildren()) {
                    Boolean isPositiveFeedback = feedbackSnapshot.child("positiveFeedback").getValue(Boolean.class);
                    Boolean isPopular = feedbackSnapshot.child("popular").getValue(Boolean.class);
                    String activityName = feedbackSnapshot.child("activity").getValue(String.class);

                    if (Boolean.TRUE.equals(isPositiveFeedback) && Boolean.TRUE.equals(isPopular)) {
                        newPopularActivityTitles.add(activityName);
                    }
                }
                updateUIWithPopularActivities(newPopularActivityTitles);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    // Method to be called when new data is fetched
    private void updateUIWithPopularActivities(ArrayList<String> newPopularActivityTitles) {
        popularAdapter.updateData(newPopularActivityTitles);
    }
}

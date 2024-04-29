package com.example.fypapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// ActivitiesWorkedForMeActivity.java
public class ActivitiesWorked extends AppCompatActivity {
    private ListView listViewActivities;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> activityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities_worked);

        listViewActivities = findViewById(R.id.listViewActivities);
        activityList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, activityList);
        listViewActivities.setAdapter(adapter);

        retrieveActivitiesWithPositiveSentimentScore();
    }

    private void retrieveActivitiesWithPositiveSentimentScore() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference feedbackRef = FirebaseDatabase.getInstance().getReference("feedback").child(user.getUid());
            Query query = feedbackRef.orderByChild("sentimentScore").startAt(70);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Map<String, Integer> activityOccurrences = new HashMap<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String activityName = snapshot.child("feedback").getValue(String.class);
                        float sentimentScore = snapshot.child("sentimentScore").getValue(Float.class);

                        if (activityName != null && sentimentScore > 50) {
                            activityOccurrences.put(activityName, activityOccurrences.getOrDefault(activityName, 0) + 1);
                        }
                    }
                    // Display activities with their occurrence count
                    for (Map.Entry<String, Integer> entry : activityOccurrences.entrySet()) {
                        String activityName = entry.getKey();
                        int occurrenceCount = entry.getValue();
                        String displayString = occurrenceCount > 1 ? activityName + " (worked " + occurrenceCount + " times)" : activityName;
                        activityList.add(displayString);
                    }

                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle any errors
                }
            });
        } else {
            // Handle case when user is not logged in
        }
    }
}
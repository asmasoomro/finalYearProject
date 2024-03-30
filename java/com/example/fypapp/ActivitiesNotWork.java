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

public class ActivitiesNotWork extends AppCompatActivity {
    private ListView listViewActivities;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> activityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities_not_work);

        listViewActivities = findViewById(R.id.listViewActivities);
        activityList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, activityList);
        listViewActivities.setAdapter(adapter);

        retrieveActivitiesWithNegativeSentimentScore();
    }

    public void retrieveActivitiesWithNegativeSentimentScore() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference feedbackRef = FirebaseDatabase.getInstance().getReference("feedback").child(user.getUid());
            Query query = feedbackRef.orderByChild("sentimentScore").endAt(50);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String activityName = snapshot.child("feedback").getValue(String.class);
                        float sentimentScore = snapshot.child("sentimentScore").getValue(Float.class);

                        if (activityName != null && sentimentScore < 50) {
                            activityList.add(activityName);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle any errors
                }
            });
        } else {
        }
    }
}

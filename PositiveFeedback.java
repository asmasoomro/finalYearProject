package com.example.fypapp;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PositiveFeedback extends AppCompatActivity {

    private ArrayList<String> positiveFeedbackList = new ArrayList<>();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_positive_feedback);

        progressBar = findViewById(R.id.progressBar);

        retrievePositiveFeedback();
    }

    private void retrievePositiveFeedback() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String loggedInUserId = user.getUid();

            DatabaseReference feedbackRef = FirebaseDatabase.getInstance().getReference("user_feedback").child(loggedInUserId);

            String activityName = getIntent().getStringExtra("activityName");

            Query query = feedbackRef.orderByChild("feedback").equalTo(activityName);

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    positiveFeedbackList.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        boolean isPositiveFeedback = snapshot.child("positiveFeedback").getValue(Boolean.class);
                        if (isPositiveFeedback) {
                            // Assuming "feedback" is a child node containing the actual feedback text
                            String feedback = snapshot.child("feedback").getValue(String.class);
                            positiveFeedbackList.add(feedback);
                        }
                    }

                    for (String feedback : positiveFeedbackList) {
                        Log.d("PositiveFeedback", feedback);
                    }


                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors, if any
                    Log.e("Firebase", "Error retrieving positive feedback", databaseError.toException());

                    // Hide the progress bar
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }
}

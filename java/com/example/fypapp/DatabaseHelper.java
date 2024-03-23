package com.example.fypapp;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.example.fypapp.model.FeedbackEntry;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ExecutionException;

public class DatabaseHelper {

    public static float getPositivePercentage(String activity) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            DatabaseReference feedbackRef = FirebaseDatabase.getInstance().getReference("user_feedback").child(userId);
            Query query = feedbackRef.orderByChild("activity").equalTo(activity);

            DataSnapshot dataSnapshot = null;
            try {
                dataSnapshot = Tasks.await(query.get());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            if (dataSnapshot != null && dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FeedbackEntry feedbackEntry = snapshot.getValue(FeedbackEntry.class);
                    if (feedbackEntry != null) {
                        return feedbackEntry.calculatePositivePercentage();
                    }
                }
            } else {
                // Log or handle the case when no feedback is found
                return 0.0f; // Default value
            }
        }
        return 0.0f;
    }

    public static void updateFeedbackAndPopularity(String activity, boolean positiveFeedback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            DatabaseReference feedbackRef = FirebaseDatabase.getInstance().getReference("user_feedback").child(userId);
            Query query = feedbackRef.orderByChild("activity").equalTo(activity);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot feedbackSnapshot : snapshot.getChildren()) {
                            FeedbackEntry feedbackEntry = feedbackSnapshot.getValue(FeedbackEntry.class);
                            if (feedbackEntry != null) {
                                feedbackEntry.setPositiveFeedback(positiveFeedback);
                                float positivePercentage = feedbackEntry.calculatePositivePercentage();
                                feedbackEntry.setPopular(positivePercentage > 70);
                                feedbackSnapshot.getRef().setValue(feedbackEntry);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle the error
                }
            });
        }
    }
}


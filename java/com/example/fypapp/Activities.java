package com.example.fypapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fypapp.model.FeedbackEntry;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;

public class Activities extends AppCompatActivity implements ParseAdapter.OnHeartIconClickListener, ParseAdapter.OnFeedbackIconClickListener {

    private static final int FEEDBACK_REQUEST_CODE = 1;
    private RecyclerView recyclerView;
    private ParseAdapter adapter;
    private ArrayList<ParseItem> parseItems = new ArrayList<>();
    private ArrayList<Float> positivePercentages = new ArrayList<>();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities);

        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ParseAdapter(parseItems, positivePercentages, this, this, this);
        recyclerView.setAdapter(adapter);


        Content content = new Content();
        content.execute();
    }

    private class Content extends AsyncTask<Void, Void, ArrayList<ParseItem>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("ContentAsyncTask", "AsyncTask started");
            progressBar.setVisibility(View.VISIBLE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(Activities.this, android.R.anim.fade_in));
        }

        @Override
        protected void onPostExecute(ArrayList<ParseItem> result) {
            super.onPostExecute(result);
            Log.d("ContentAsyncTask", "AsyncTask completed");
            Log.d("ContentAsyncTask", "AsyncTask completed. Result size: " + result.size());

            parseItems = result;
            positivePercentages.clear(); // Clear existing data
            for (ParseItem item : result) {
                positivePercentages.add(item.getPositivePercentage());
            }

            runOnUiThread(() -> {
                Collections.sort(result, (item1, item2) -> {
                    if (item1.isPopular() && !item2.isPopular()) {
                        return -1;
                    } else if (!item1.isPopular() && item2.isPopular()) {
                        return 1;
                    } else {
                        return Float.compare(item2.getPositivePercentage(), item1.getPositivePercentage());
                    }
                });

                adapter.setItems(result, positivePercentages);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                progressBar.startAnimation(AnimationUtils.loadAnimation(Activities.this, android.R.anim.fade_out));
                fetchAndDisplayActivitiesWithSentimentScore();
            });
        }
        private void fetchAndDisplayActivitiesWithSentimentScore() {
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("feedback");
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                databaseRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            FeedbackEntry entry = snapshot.getValue(FeedbackEntry.class);
                            if (entry != null) {
                                String activityName = entry.getActivityName(); // This should now return the correct name
                                Log.d("FirebaseData", "Fetched activity: " + activityName + " with score: " + entry.getSentimentScore());
                                for (int i = 0; i < parseItems.size(); i++) {
                                    ParseItem parseItem = parseItems.get(i);
                                    if (parseItem.getTitle().equals(activityName)) {
                                        String titleWithEmoji = parseItem.getTitle();
                                        if (entry.getSentimentScore() > 50) {
                                            titleWithEmoji += " ✅";
                                            Log.d("FirebaseData", "Added tick to: " + activityName);
                                        } else {
                                            titleWithEmoji += " ❌";
                                            Log.d("FirebaseData", "Added cross to: " + activityName);
                                        }
                                        parseItem.setTitle(titleWithEmoji);
                                        break; // No need to continue the loop after updating the item
                                    }
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("Firebase", "Failed to read sentiment scores", databaseError.toException());
                    }
                });
            }
        }

        @Override
        protected ArrayList<ParseItem> doInBackground(Void... voids) {
            ArrayList<ParseItem> result = new ArrayList<>();
            try {
                String url = "https://www.happierhuman.com/mood-boosting-activities/";

                Document doc = Jsoup.connect(url).get();

                Elements data = doc.select("h3");
                int size = data.size();
                for (int i = 0; i < size; i++) {
                    String activity = data.get(i).text();
                    Log.d("ContentAsyncTask", "Activity from website: " + activity);

                    // Fetch feedback entries for the activity from Firebase Realtime Database
                    float positivePercentage = getPositivePercentageSafely(activity);

                    boolean isPopular = positivePercentage > 70;

                    // Create ParseItem instance with activity details and update popularity
                    ParseItem item = new ParseItem(activity, positivePercentage, isPopular);
                    result.add(item);
                }
                Log.d("AsyncTask", "Number of items retrieved: " + result.size());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }


        private float getPositivePercentageSafely(String activity) {
            DatabaseReference feedbackRef = FirebaseDatabase.getInstance().getReference("user_feedback");
            Query query = feedbackRef.orderByChild("feedback").equalTo(activity);

            try {
                DataSnapshot dataSnapshot = Tasks.await(query.get());
                if (dataSnapshot.exists()) {
                    int totalFeedbackCount = 0;
                    int positiveFeedbackCount = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        boolean positiveFeedback = snapshot.child("positiveFeedback").getValue(Boolean.class);
                        if (positiveFeedback) {
                            positiveFeedbackCount++;
                        }
                        totalFeedbackCount++;
                    }
                    if (totalFeedbackCount > 0) {
                        return ((float) positiveFeedbackCount / totalFeedbackCount) * 100;
                    }
                } else {
                    Log.d("PositivePercentage", "No feedback found for " + activity);
                }
            } catch (ExecutionException | InterruptedException | DatabaseException e) {
                Log.e("Firebase Error", "Error retrieving feedback for " + activity + ": " + e.getMessage());
            }

            return 0.0f; // Default value
        }
    }

        private float getPositivePercentage(String activity) throws ExecutionException, InterruptedException {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();

                DatabaseReference feedbackRef = FirebaseDatabase.getInstance().getReference("user_feedback").child(userId);
                Query query = feedbackRef.orderByChild("activity").equalTo(activity);

                try {
                    // Perform the blocking operation in the background
                    DataSnapshot dataSnapshot = Tasks.await(query.get());

                    if (dataSnapshot != null && dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            FeedbackEntry feedbackEntry = snapshot.getValue(FeedbackEntry.class);
                            if (feedbackEntry != null && feedbackEntry.calculatePositivePercentage() > 70) {
                                return feedbackEntry.calculatePositivePercentage();
                            }
                        }
                    } else {
                        Log.d("PositivePercentage", "No feedback found for " + activity);
                        return 0.0f; // Default value
                    }

                    return 0.0f; // Default value if nothing is found
                } catch (ExecutionException | InterruptedException e) {
                    // Handle exceptions
                    throw e; // Propagate the exception
                }
            }

            return 0.0f; // Default value if user is not logged in
        }


    private void loadPopularActivities() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("user_feedback");
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> popularActivityTitles = new ArrayList<>();
                for (DataSnapshot feedbackSnapshot : dataSnapshot.getChildren()) {
                    Boolean isPositiveFeedback = feedbackSnapshot.child("positiveFeedback").getValue(Boolean.class);
                    Boolean isPopular = feedbackSnapshot.child("popular").getValue(Boolean.class);
                    String activityName = feedbackSnapshot.child("activity").getValue(String.class);

                    if (isPositiveFeedback != null && isPositiveFeedback && isPopular != null && isPopular) {
                        popularActivityTitles.add(activityName);
                    }
                }

                if (!popularActivityTitles.isEmpty()) {
                    // Display or handle the list of popular activity titles with positive feedback here
                    // For example, update a RecyclerView adapter
                    updateUIWithPopularActivities(popularActivityTitles);
                } else {
                    // Handle case where there are no popular activities with positive feedback
                    Toast.makeText(Activities.this, "No popular activities with positive feedback found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors
                Toast.makeText(Activities.this, "Error loading popular activities: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUIWithPopularActivities(ArrayList<String> popularActivityTitles) {

    }

    private void retrievePositiveFeedbackForPopularActivities(ArrayList<String> popularActivityTitles) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference feedbackRef = FirebaseDatabase.getInstance().getReference("user_feedback").child(userId);

            feedbackRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot feedbackSnapshot : dataSnapshot.getChildren()) {
                        String feedbackText = feedbackSnapshot.child("feedback").getValue(String.class);
                        boolean isPositiveFeedback = feedbackSnapshot.child("positiveFeedback").getValue(Boolean.class);
                        String activityName = feedbackSnapshot.child("activity").getValue(String.class);

                        if (popularActivityTitles.contains(activityName) && isPositiveFeedback) {
                            Log.d("Positive Feedback", "Feedback for " + activityName + ": " + feedbackText);
                            // Handle or display positive feedback here
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle errors here
                }
            });
        }
    }


    private void updateDatabase(String activity, boolean favorited) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser != null ? currentUser.getUid() : null;

        if (userId != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("favorited_activities")
                    .child(userId)
                    .child(sanitizeKey(activity));
            databaseReference.setValue(favorited);
        }
    }

    private String sanitizeKey(String key) {
        return key.replaceAll("[^a-zA-Z0-9 ]", "_");
    }

    @Override
    public void onHeartIconClick(String activity, boolean favorited) {
        updateDatabase(activity, favorited);
    }

    @Override
    public void onFeedbackIconClick(String activity) {
        Intent feedbackIntent = new Intent(Activities.this, Feedback2.class);
        feedbackIntent.putExtra("activityName", activity);
        startActivityForResult(feedbackIntent, FEEDBACK_REQUEST_CODE);
    }
    public void updateFeedbackStatus(String activityTitle, boolean isPositiveFeedback) {
        for (int i = 0; i < parseItems.size(); i++) {
            ParseItem item = parseItems.get(i);
            if (item.getTitle().equalsIgnoreCase(activityTitle)) {
                item.setFeedbackPositive(isPositiveFeedback);

                adapter.notifyItemChanged(i);
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FEEDBACK_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String activityName = data.getStringExtra("activityName"); // This now matches the new key
            boolean isPositiveFeedback = data.getBooleanExtra("isPositiveFeedback", false); // Retrieve the new data
            float positivePercentage = data.getFloatExtra("positivePercentage", 0.0f); // Existing line, if still needed
            updateFeedbackStatus(activityName, isPositiveFeedback);
        }
    }

}


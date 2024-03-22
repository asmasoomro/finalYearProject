package com.example.fypapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;

import com.example.fypapp.model.FeedbackEntry;

public class PositiveActivities extends AppCompatActivity implements ParseAdapter.OnHeartIconClickListener, ParseAdapter.OnFeedbackIconClickListener {


    private static final int FEEDBACK_REQUEST_CODE = 1;
    private RecyclerView recyclerView;
    private ParseAdapter adapter;
    private ArrayList<ParseItem> parseItems = new ArrayList<>();
    private ArrayList<Float> positivePercentages = new ArrayList<>();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_positive_activities);

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
            progressBar.setVisibility(View.VISIBLE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(PositiveActivities.this, android.R.anim.fade_in));
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
                progressBar.startAnimation(AnimationUtils.loadAnimation(PositiveActivities.this, android.R.anim.fade_out));
            });
        }

        @Override
        protected ArrayList<ParseItem> doInBackground(Void... voids) {
            ArrayList<ParseItem> result = new ArrayList<>();
            try {
                String url = "https://www.powerofpositivity.com/positive-mind-activities/";

                Document doc = Jsoup.connect(url).get();

                Elements data = doc.select("h3");
                int size = data.size();
                for (int i = 0; i < size; i++) {
                    String activity = data.get(i).text();
                    float positivePercentage = getPositivePercentage(activity);
                    boolean isPopular = positivePercentage > 70;
                    result.add(new ParseItem(activity, positivePercentage, isPopular));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        private float getPositivePercentage(String activity) {
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
                        if (feedbackEntry != null && feedbackEntry.calculatePositivePercentage() > 70) {
                            return feedbackEntry.calculatePositivePercentage();
                        }
                    }
                } else {
                    Log.d("PositivePercentage", "No feedback found for " + activity);
                    return 0.0f; // Default value
                }
            }
            return 0.0f;
        }
    }

    private void updateDatabase(String activity, boolean favorited) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser != null ? currentUser.getUid() : null;

        if (userId != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("favorited_activities")
                    .child(userId)
                    .child(activity);
            databaseReference.setValue(favorited);
        }
    }

    @Override
    public void onHeartIconClick(String activity, boolean favorited) {
        updateDatabase(activity, favorited);
    }

    @Override
    public void onFeedbackIconClick(String activity) {
        Intent feedbackIntent = new Intent(PositiveActivities.this, Feedback2.class);
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

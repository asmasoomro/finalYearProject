package com.example.fypapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Activities extends AppCompatActivity implements ParseAdapter.OnHeartIconClickListener {

    private RecyclerView recyclerView;
    private ParseAdapter adapter;
    private ArrayList<ParseItem> parseItems = new ArrayList<>();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities);

        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ParseAdapter(parseItems, this, this);
        recyclerView.setAdapter(adapter);

        Content content = new Content();
        content.execute();
    }

    private class Content extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(Activities.this, android.R.anim.fade_in));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(Activities.this, android.R.anim.fade_out));
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String url = "https://www.happierhuman.com/mood-boosting-activities/";

                Document doc = Jsoup.connect(url).get();

                Elements data = doc.select("h3.wp-block-heading");
                int size = data.size();
                for (int i = 0; i < size; i++) {
                    String activity = data.get(i).text();
                    parseItems.add(new ParseItem(activity));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    // Implement the OnHeartIconClickListener method
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
        // Replace invalid characters with underscores
        return key.replaceAll("[^a-zA-Z0-9 ]", "_");
    }




    @Override
    public void onHeartIconClick(String activity, boolean favorited) {
        updateDatabase(activity, favorited);
    }
}

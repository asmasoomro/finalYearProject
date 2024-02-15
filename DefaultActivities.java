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

public class DefaultActivities extends AppCompatActivity implements ParseAdapter.OnHeartIconClickListener {

    private RecyclerView recyclerView;
    private ParseAdapter adapter;
    private ArrayList<ParseItem> parseItems = new ArrayList<>();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_activities);

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
            progressBar.startAnimation(AnimationUtils.loadAnimation(DefaultActivities.this, android.R.anim.fade_in));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(DefaultActivities.this, android.R.anim.fade_out));
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String url = "https://www.myclevermind.com/knowledge/joyful-activities-to-make-you-happy/";

                Document doc = Jsoup.connect(url).get();

                Elements data = doc.select("h2");
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
        return key.replaceAll("[^a-zA-Z0-9]", " ");
    }



    @Override
    public void onHeartIconClick(String activity, boolean favorited) {
        updateDatabase(activity, favorited);
    }
}

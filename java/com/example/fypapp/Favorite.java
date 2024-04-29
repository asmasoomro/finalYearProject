package com.example.fypapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.fypapp.Adapter.FavoriteAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Favorite extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FavoriteAdapter adapter;
    private ArrayList<ParseItem> favoritedActivities = new ArrayList<>();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FavoriteAdapter(favoritedActivities, this);
        recyclerView.setAdapter(adapter);

        // Retrieve favorited activities for the logged-in user
        retrieveFavoritedActivities();
    }

    private void retrieveFavoritedActivities() {
        // Logged in user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String loggedInUserId = user.getUid();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                    .getReference("favorited_activities")
                    .child(loggedInUserId);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    favoritedActivities.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String favoritedActivityTitle = snapshot.getKey();
                        boolean isFavorited = snapshot.getValue(Boolean.class);

                        // Create a ParseItem object and add it to the list
                        ParseItem favoritedItem = new ParseItem(favoritedActivityTitle, isFavorited);
                        favoritedItem.setFavorited(isFavorited);
                        favoritedActivities.add(favoritedItem);
                    }

                    // Notify the adapter that the data has changed
                    adapter.notifyDataSetChanged();

                    // Hide the progress bar
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors, if any
                    Log.e("Firebase", "Error retrieving favorited activities", databaseError.toException());

                    // Hide the progress bar
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }
}



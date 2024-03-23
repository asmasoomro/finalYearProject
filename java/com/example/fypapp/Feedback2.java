package com.example.fypapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fypapp.model.FeedbackEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Feedback2 extends AppCompatActivity {
    private EditText editTextBefore, editTextDuring, editTextAfter;
    private Button submitButton;
    private TextView txvResult;
    private TextView txtnegative;
    private TextView txtpositive;
    private ArrayList<ParseItem> parseItems;
    private ArrayList<Float> positivePercentages;
    private ParseAdapter adapter;
    private ImageView imageViewTick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback2);

        editTextBefore = findViewById(R.id.editTextBefore);
        editTextDuring = findViewById(R.id.editTextDuring);
        editTextAfter = findViewById(R.id.editTextAfter);
        submitButton = findViewById(R.id.submitButton);
        txtnegative = findViewById(R.id.txtnegative);
        txtpositive = findViewById(R.id.txtpositive);
        txvResult = findViewById(R.id.txvResult);
        imageViewTick = findViewById(R.id.imageViewTick);


        parseItems = new ArrayList<>();
        positivePercentages = new ArrayList<>();
        adapter = new ParseAdapter(parseItems, positivePercentages, this, null, null);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFeedback();
            }
        });
    }

    private void submitFeedback() {
        String feelingsBefore = editTextBefore.getText().toString();
        String feelingsDuring = editTextDuring.getText().toString();
        String feelingsAfter = editTextAfter.getText().toString();

        //String userFeedback = feelingsBefore + " " + feelingsDuring + " " + feelingsAfter;
        String userFeedback = feelingsAfter;
        String activityName = getIntent().getStringExtra("activityName");

        runSentimentAnalysis(userFeedback, activityName);
    }

    private void runSentimentAnalysis(String userFeedback, String activityName) {
        if (!userFeedback.isEmpty()) {
            String getURL = "https://api.uclassify.com/v1/asmasoomro/sentiment1/classify/?readKey=Eq0AQctXIjEh&text=" + userFeedback;
            OkHttpClient client = new OkHttpClient();
            try {
                Request request = new Request.Builder()
                        .url(getURL)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        System.out.println(e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final JSONObject jsonResult;
                        final String result = response.body().string();
                        try {
                            jsonResult = new JSONObject(result);
                            final String convertedText = jsonResult.getString("positive1");
                            final String convertedText1 = jsonResult.getString("negative1");

                            Log.d("okHttp", jsonResult.toString());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    float positivePercentage = Float.parseFloat(convertedText) * 100;
                                    float negativePercentage = Float.parseFloat(convertedText1) * 100;
                                    for (ParseItem item : parseItems) {
                                        if (item.getTitle().equals(activityName)) {
                                            item.setFeedbackPositive(positivePercentage > 70);
                                            break; // No need to continue the loop
                                        }
                                    }
                                    adapter.setItems(parseItems, positivePercentages);

                                    // Save feedback to database
                                    saveFeedbackToDatabase(positivePercentage, negativePercentage, activityName);


                                    txtpositive.setText("Positive1: " + String.valueOf(positivePercentage) + "%");
                                    txtnegative.setText("Negative1: " + String.valueOf(negativePercentage) + "%");

                                    finish();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception ex) {
                txtpositive.setText(ex.getMessage());
                txtnegative.setText(ex.getMessage());
            }
        }
    }

    private void saveFeedbackToDatabase(float positivePercentage, float negativePercentage, String activityName) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference feedbackRef = FirebaseDatabase.getInstance().getReference("feedback").child(user.getUid());

            String feedbackId = feedbackRef.push().getKey(); // Generate a unique key for the feedback entry
            FeedbackEntry feedbackEntry = new FeedbackEntry(activityName, positivePercentage, negativePercentage);

            if (feedbackId != null) {
                feedbackRef.child(feedbackId).setValue(feedbackEntry)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Feedback saved successfully
                                Log.d("Feedback", "Feedback saved successfully");
                            } else {
                                // Failed to save feedback
                                Log.d("Feedback", "Failed to save feedback");
                            }
                        });
            }
        }
    }
}
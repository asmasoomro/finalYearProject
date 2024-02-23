package com.example.fypapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.fypapp.model.FeedbackEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Feedback extends AppCompatActivity {
    private EditText editTextBefore, editTextDuring, editTextAfter;
    private Button submitButton;
    private TextView txvResult;
    private TextView txtnegative;
    private TextView txtpositive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        editTextBefore = findViewById(R.id.editTextBefore);
        editTextDuring = findViewById(R.id.editTextDuring);
        editTextAfter = findViewById(R.id.editTextAfter);
        submitButton = findViewById(R.id.submitButton);
        txtnegative = findViewById(R.id.txtnegative);
        txtpositive = findViewById(R.id.txtpositive);
        txvResult = findViewById(R.id.txvResult);

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

        // Combine all answers into one big one
        String userFeedback = feelingsBefore + " " + feelingsDuring + " " + feelingsAfter;

        // Run the sentiment analysis
        runSentimentAnalysis(feelingsAfter);
    }

    private void runSentimentAnalysis(String userFeedback) {
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

                                    storeFeedbackInDatabase(userFeedback, positivePercentage);

                                    txtpositive.setText("Positive1: " + String.valueOf(positivePercentage) + "%");
                                    txtnegative.setText("Negative1: " + String.valueOf(negativePercentage) + "%");

                                    storeSentimentPercentages(positivePercentage, negativePercentage);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                txtpositive.setText(ex.getMessage());
                txtnegative.setText(ex.getMessage());
            }
        }
    }


    private void storeSentimentPercentages(float positivePercentage, float negativePercentage) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference sentimentRef = FirebaseDatabase.getInstance().getReference("user_sentiment").child(userId);

            sentimentRef.child("positivePercentage").setValue(positivePercentage);
            sentimentRef.child("negativePercentage").setValue(negativePercentage);
        }
    }

    private void storeFeedbackInDatabase(String feedback, float sentimentScore){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference feedbackRef = FirebaseDatabase.getInstance().getReference("user_feedback").child(userId);
            String feedbackId = feedbackRef.push().getKey();

            FeedbackEntry feedbackEntry = new FeedbackEntry(feedback, sentimentScore);
            feedbackRef.child(feedbackId).setValue(feedbackEntry);
        }
    }

}


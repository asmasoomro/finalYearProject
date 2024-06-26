package com.example.fypapp;
import android.Manifest;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomePage extends AppCompatActivity implements View.OnClickListener{
    private Button ButtonAnalysis;
    private TextView textViewUserEmail;
    private FirebaseAuth firebaseAuth;
    private TextView txvResult;
    private TextView txtnegative;
    private TextView txtpositive;
    String sourceText;
    private DatabaseReference moodDatabaseRef;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        moodDatabaseRef = FirebaseDatabase.getInstance().getReference("user_moods");
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(HomePage.this, MainActivity.class));
        }
        FirebaseUser user = firebaseAuth.getCurrentUser();
        txtnegative = findViewById(R.id.txtnegative);
        txtpositive = findViewById(R.id.txtpositive);
        txvResult = findViewById(R.id.txvResult);
        textViewUserEmail = findViewById(R.id.textViewUserEmail);
        String[] temp = user.getEmail().toString().split("@");
        textViewUserEmail.setText("Welcome " + temp[0]);
        ButtonAnalysis = findViewById(R.id.ButtonAnalysis);
        ButtonAnalysis.setOnClickListener(this);
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
        }
    }


    public void getSpeechInput(View view) {
        txvResult.setText(null);
        txtpositive.setText(null);
        txtnegative.setText(null);
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM );
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Your Device does not support speech input", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if(resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txvResult.setText(result.get(0));
                }
                break;
        }
    }

    public void analyseText() {
        sourceText = txvResult.getText().toString();
        String getURL = "https://api.uclassify.com/v1/uClassify/Sentiment/classify/?readKey=9QRZ6ZWbVRpH&text="+sourceText;

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
                        final String convertedText = jsonResult.getString("positive");
                        final String convertedText1 = jsonResult.getString("negative");

                        Log.d("okHttp", jsonResult.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                float a;
                                a=Float.parseFloat(convertedText);
                                a=a*100;
                                txtpositive.setText("Positive: " +String.valueOf(a) + "%");
                                a=Float.parseFloat(convertedText1);
                                a=a*100;
                                txtnegative.setText("Negative:" +String.valueOf(a) + "%");
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

    private void saveMoodToDatabase(float positivePercentage, float negativePercentage) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userMoodRef = moodDatabaseRef.child(userId).child("moods");

            String timestamp = String.valueOf(System.currentTimeMillis());

            Mood mood = new Mood(positivePercentage, negativePercentage);

            // userMoodRef.child(timestamp).setValue(mood);
            DatabaseReference moodEntryRef = userMoodRef.child(timestamp);
            moodEntryRef.setValue(mood);

            DatabaseReference positivePercentageRef = moodEntryRef.child("positivePercentage");
            positivePercentageRef.setValue(positivePercentage);
        }
    }
    private void suggestActivities(float positivePercentage, float negativePercentage) {
        if (positivePercentage > 70) {
            suggestPositiveActivities();
        } else if (negativePercentage > 70) {
            suggestActivitiesToImproveMood();
        } else {
            suggestDefaultActivities();
        }
    }

    private void suggestPositiveActivities() {
        Intent intent = new Intent(HomePage.this, PositiveActivities.class);
        startActivity(intent);
        finish();
    }

    private void suggestActivitiesToImproveMood() {
        Intent intent = new Intent(HomePage.this, Activities.class);
        startActivity(intent);
        finish();
    }

    private void suggestDefaultActivities() {
        Intent intent = new Intent(HomePage.this, DefaultActivities.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {

        if (view == ButtonAnalysis) {
            analyseText();
        }

    }


}
package com.example.fypapp;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class Journal extends AppCompatActivity implements View.OnClickListener {
    private Button ButtonAnalysis;
    private TextView textViewUserEmail;
    private FirebaseAuth firebaseAuth;
    private TextView txvResult;
    private TextView txtnegative;
    private TextView txtpositive;
    String sourceText;
    private DatabaseReference moodDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);
        moodDatabaseRef = FirebaseDatabase.getInstance().getReference("user_moods");
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(Journal.this, MainActivity.class));
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

    }

    public void analyseText() {
        EditText journal = findViewById(R.id.journal);
        sourceText = journal.getText().toString();
        if (!sourceText.isEmpty()) {
            String getURL = "https://api.uclassify.com/v1/asmasoomro/sentiment1/classify/?readKey=Eq0AQctXIjEh&text=" + sourceText;

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

                                    saveMoodToDatabase(positivePercentage, negativePercentage);

                                    txtpositive.setText("Positive1: " + String.valueOf(positivePercentage) + "%");
                                    txtnegative.setText("Negative1: " + String.valueOf(negativePercentage) + "%");

                                    suggestActivities(positivePercentage, negativePercentage);
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
    public void getSpeechInput(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
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
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String speechInput = result.get(0);
                    EditText journal = findViewById(R.id.journal);
                    journal.setText(speechInput);
                    analyseText();
                }
                break;
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
        Intent intent = new Intent(Journal.this, PositiveActivities.class);
        startActivity(intent);
        finish();
    }

    private void suggestActivitiesToImproveMood() {
        Intent intent = new Intent(Journal.this, Activities.class);
        startActivity(intent);
        finish();
    }

    private void suggestDefaultActivities() {
        Intent intent = new Intent(Journal.this, DefaultActivities.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onClick (View view){
        //  Intent intent = new Intent(Journal.this, Activities.class);
        //   startActivity(intent);
//        finish();

        if (view == ButtonAnalysis) {
            analyseText();
        }
    }
}
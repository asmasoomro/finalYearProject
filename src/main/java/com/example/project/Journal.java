package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.journal);
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
                                    float a;
                                    a = Float.parseFloat(convertedText);
                                    a = a * 100;
                                    txtpositive.setText("Positive1: " + String.valueOf(a) + "%");
                                    a = Float.parseFloat(convertedText1);
                                    a = a * 100;
                                    txtnegative.setText("Negative1:" + String.valueOf(a) + "%");
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


    @Override
    public void onClick (View view){
        Intent intent = new Intent(Journal.this, Activities.class);
        startActivity(intent);
        finish();

        if (view == ButtonAnalysis) {
            analyseText();
        }
    }
}
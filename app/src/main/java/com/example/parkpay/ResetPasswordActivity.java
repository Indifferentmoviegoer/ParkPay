package com.example.parkpay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputEditText email;
    private Context c;

    private String mail="";

    private static final String TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.MaterialTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        email = findViewById(R.id.email);
        AppCompatButton reset = findViewById(R.id.reset);
        AppCompatButton cancel = findViewById(R.id.cancel);

        c = this;

        reset.setOnClickListener(view -> {

            mail= Objects.requireNonNull(email.getText()).toString();
            resetPass();
        });

        cancel.setOnClickListener(view -> {

            Intent intent = new Intent(c,
                    SignInActivity.class);
            startActivity(intent);
        });
    }

    private void resetPass(){

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        try {
            json.put("email",mail);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonString = json.toString();
        RequestBody body = RequestBody.create(JSON, jsonString);
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .post(body)
                .url("https://api.mobile.goldinnfish.com/reset_pass")
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                if(call.request().body()!=null)
                {
                    Log.d(TAG, Objects.requireNonNull(call.request().body()).toString());
                }

                runOnUiThread(() -> {
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) {
                runOnUiThread(() -> {

                    try {

                        String jsonData = null;
                        if (response.body() != null) {
                            jsonData = response.body().string();
                        }

                        JSONObject Jobject = new JSONObject(Objects.requireNonNull(jsonData));

                        Toast.makeText(getApplicationContext(),
                                Jobject.getString("msg"),
                                Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(c,
                                SignInActivity.class);
                        startActivity(intent);


                    } catch (IOException | JSONException e) {

                        Toast.makeText(getApplicationContext(),"Пользователь с таким email не существует!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}
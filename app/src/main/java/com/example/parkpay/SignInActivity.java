package com.example.parkpay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

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

public class SignInActivity extends AppCompatActivity {
    private TextInputEditText login;
    private TextInputEditText pass;
    private String loginUser;
    private String passwordUser;
    private Context c;
    private SharedPreferences settings;
    private static final String APP_PREFERENCES = "mysettings";
    private static final String APP_PREFERENCES_PASSWORD ="Password";
    private static final String APP_PREFERENCES_TOKEN ="Token";
    private static final String APP_PREFERENCES_LOGIN ="Login";
    private static final String TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.MaterialTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        AppCompatButton signIn = findViewById(R.id.signIn);
        login = findViewById(R.id.email);
        pass =findViewById(R.id.password);
        AppCompatButton help = findViewById(R.id.helper);
        AppCompatTextView remember = findViewById(R.id.remember);

        settings=getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        c=this;

        remember.setOnClickListener(view -> {

            Intent intent = new Intent(c,
                    ResetPasswordActivity.class);
            startActivity(intent);
        });



//        TypefaceUtil.overrideFont(getApplicationContext(), "serif", "font/roboto_regular.ttf");

        signIn.setOnClickListener(v -> {
            loginUser= Objects.requireNonNull(login.getText()).toString();
            passwordUser= Objects.requireNonNull(pass.getText()).toString();
            if (loginUser.equals("") || loginUser.length() == 0 ||
                    passwordUser.equals("") || passwordUser.length() == 0) {
                Toast.makeText(getApplicationContext(), "Заполните все поля ввода!",
                        Toast.LENGTH_SHORT).show();
            }
            else {

                boolean checkConnection=MainActivity.isOnline(c);

                    if(checkConnection){

                        signIn();
                    }
                    else {
                        Toast.makeText(getApplicationContext(),
                                "Отсутствует интернет соединение!",
                                Toast.LENGTH_SHORT).show();
                    }
            }
        });

        if(settings.contains(APP_PREFERENCES_TOKEN)){

            refreshToken();


            Intent intent = new Intent(SignInActivity.this,
                    MainActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);

        }
        help.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });


        if(settings.contains(APP_PREFERENCES_LOGIN)
                &&settings.contains(APP_PREFERENCES_PASSWORD)) {

                login.setText(settings.getString(APP_PREFERENCES_LOGIN, ""));
                pass.setText(settings.getString(APP_PREFERENCES_PASSWORD, ""));

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(APP_PREFERENCES_LOGIN, Objects.requireNonNull(login.getText()).toString());
        editor.putString(APP_PREFERENCES_PASSWORD, Objects.requireNonNull(pass.getText()).toString());
        editor.apply();
    }

    private void signIn(){

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        try {
            json.put("login",loginUser);
            json.put("password",passwordUser);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonString = json.toString();
        RequestBody body = RequestBody.create(JSON, jsonString);
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .post(body)
                .url("https://api.mobile.goldinnfish.com/login")
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

                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(APP_PREFERENCES_TOKEN,Jobject.getString("token"));
                        editor.apply();

                        if(settings.contains(APP_PREFERENCES_TOKEN)) {

                            Toast.makeText(getApplicationContext(), "Вход",
                                    Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(SignInActivity.this,
                                    MainActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                        }

                    } catch (IOException | JSONException e) {

                        Toast.makeText(getApplicationContext(), "Неверный логин или пароль!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void refreshToken(){

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        try {
            json.put("login",settings.getString(APP_PREFERENCES_LOGIN,""));
            json.put("password",settings.getString(APP_PREFERENCES_PASSWORD,""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonString = json.toString();
        RequestBody body = RequestBody.create(JSON, jsonString);
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .post(body)
                .url("https://api.mobile.goldinnfish.com/login")
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

                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(APP_PREFERENCES_TOKEN,Jobject.getString("token"));
                        editor.apply();

                    } catch (IOException | JSONException e) {

                        Log.d(TAG,"Ошибка: "+e);
                    }
                });
            }
        });
    }

}
package com.example.parkpay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    Button signIn;
    EditText login;
    EditText pass;
    TextView help;
    CheckBox remember;
    String loginUser;
    String passwordUser;
    Context c;
    SharedPreferences settings;
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_CHECK ="CHECK_TRUE";
    public static final String APP_PREFERENCES_EMAIL ="Email";
    public static final String APP_PREFERENCES_PASSWORD ="Password";
    public static final String APP_PREFERENCES_TOKEN ="Token";
    private static final String TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        signIn = (Button) findViewById(R.id.signIn);
        login = (EditText) findViewById(R.id.email);
        pass = (EditText) findViewById(R.id.password);
        help = (TextView) findViewById(R.id.helper);
        remember = (CheckBox) findViewById(R.id.remember);
        settings=getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        c=this;

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser=login.getText().toString();
                passwordUser=pass.getText().toString();
                if (loginUser.equals("") || loginUser.length() == 0 ||
                        passwordUser.equals("") || passwordUser.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Заполните все поля ввода!",
                            Toast.LENGTH_SHORT).show();
                }
                else {

                    doPostRequest("http://192.168.252.199/login");

                    if(settings.contains(APP_PREFERENCES_TOKEN)) {

                        Intent intent = new Intent(SignInActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                    }
                    else {

                        Toast.makeText(getApplicationContext(), "Неверный логин или пароль!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        if(settings.contains(APP_PREFERENCES_TOKEN)){
            Intent intent = new Intent(SignInActivity.this,
                    MainActivity.class);
            startActivity(intent);
        }
        help.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        if(settings.contains(APP_PREFERENCES_CHECK)&&settings.contains(APP_PREFERENCES_EMAIL)
                &&settings.contains(APP_PREFERENCES_PASSWORD)) {
            remember.setChecked(settings.getBoolean(APP_PREFERENCES_CHECK, false));

            if(settings.getBoolean(APP_PREFERENCES_CHECK, false)){
                login.setText(settings.getString(APP_PREFERENCES_EMAIL, ""));
                pass.setText(settings.getString(APP_PREFERENCES_PASSWORD, ""));
            }

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(APP_PREFERENCES_CHECK,remember.isChecked() );
        editor.putString(APP_PREFERENCES_EMAIL,login.getText().toString());
        editor.putString(APP_PREFERENCES_PASSWORD,pass.getText().toString());
        editor.apply();
    }

    public void doPostRequest(String url){

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
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.v("TAG", Objects.requireNonNull(call.request().body()).toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                runOnUiThread(() -> {
                    try {

                        String jsonData = null;
                        if (response.body() != null) {
                            jsonData = response.body().string();
                        }
                        JSONObject Jobject = new JSONObject(jsonData);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(APP_PREFERENCES_TOKEN,Jobject.getString("token"));
                        editor.apply();
                    } catch (IOException | JSONException e) {
                        Toast.makeText(c,"Ошибка",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}
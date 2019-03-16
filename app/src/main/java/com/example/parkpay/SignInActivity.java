package com.example.parkpay;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_CHECK ="CHECK_TRUE";
    public static final String APP_PREFERENCES_EMAIL ="Email";
    public static final String APP_PREFERENCES_PASSWORD ="Password";
    public static final String APP_PREFERENCES_TOKEN ="Token";
    SharedPreferences settings;
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

        signIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loginUser=login.getText().toString();
                passwordUser=login.getText().toString();
                if (loginUser.equals("") || loginUser.length() == 0 ||
                        passwordUser.equals("") || passwordUser.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Заполните все поля ввода!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    //doGetRequest("http://www.mobile.ru/login");
                    //if(settings.contains(APP_PREFERENCES_TOKEN)){
                        Intent intent = new Intent(SignInActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                    //}
                    //if(!settings.contains(APP_PREFERENCES_TOKEN)) {
                    //    Toast.makeText(getApplicationContext(), "Неверный логин или пароль!",
                    //            Toast.LENGTH_SHORT).show();
                    //}

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
                Intent intent = new Intent(SignInActivity.this, SingUpActivity.class);
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


    private void doGetRequest(String url){
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
            public void onFailure(Call call, IOException e) {
                Log.v("TAG", call.request().body().toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        sovet.setText("Ошибка скоро будет исправлена,потерпи блять!");
                    }
                });
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                runOnUiThread(() -> {
                    try {

                        String jsonData = response.body().string();
                        JSONObject Jobject = new JSONObject(jsonData);
                        SharedPreferences.Editor editor = settings.edit();
                        Log.d(TAG, Jobject.getString("token"));
                        editor.putString(APP_PREFERENCES_TOKEN,Jobject.getString("token"));
                        editor.apply();
//                            tok = Jobject.getString("text");
//                            sovet.startAnimation(alpha_in);
//                            sovet.setText(tok);

                    } catch (IOException | JSONException e) {
//                            tok = "";
//                            sovet.setText(tok);
                    }
                });
            }
        });
    }
}
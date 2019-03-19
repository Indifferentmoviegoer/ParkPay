package com.example.parkpay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

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
                passwordUser=login.getText().toString();
                if (loginUser.equals("") || loginUser.length() == 0 ||
                        passwordUser.equals("") || passwordUser.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Заполните все поля ввода!",
                            Toast.LENGTH_SHORT).show();
                } else {
//                    JSONObject json = new JSONObject();
//                    try {
//                        json.put("login",loginUser);
//                        json.put("password",passwordUser);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    MainActivity.doGetRequest("http://www.mobile.ru/login",
//                            json,c,settings,SignInActivity.this);
//                    if(settings.contains(APP_PREFERENCES_TOKEN)) {
                    Intent intent = new Intent(SignInActivity.this,
                            MainActivity.class);
                    startActivity(intent);
//                    finish();
//                    }
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
}
package com.example.parkpay;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;

public class SignInActivity extends AppCompatActivity {
    Button signIn;
    EditText login;
    EditText pass;
    TextView help;
    CheckBox remember;
    public static final String APP_PREFERENCES = "mysettings";
    public static final Boolean APP_PREFERENCES_NAME =false;
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        signIn = (Button) findViewById(R.id.signIn);
        login = (EditText) findViewById(R.id.email);
        pass = (EditText) findViewById(R.id.password);
        help = (TextView) findViewById(R.id.helper);
        remember = (CheckBox) findViewById(R.id.remember);
        //settings=getSharedPreferences(APP_PREFERENCES,Context.)

        signIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (login.getText().toString().equals("") || login.getText().length() == 0 ||
                        pass.getText().toString().equals("") || pass.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Заполните все поля ввода!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(SignInActivity.this,
                            MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        help.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, SingUpActivity.class);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        //SharedPreferences.Editor editor=
    }
    //    private void doGetRequest(String url) {
//
//        OkHttpClient client = new OkHttpClient();
//        final Request request = new Request.Builder()
//                .url(url)
//                .build();
//        Call call = client.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.v("TAG", call.request().body().toString());
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
////                        sovet.setText("Ошибка скоро будет исправлена,потерпи блять!");
//                    }
//                });
//            }
//            @Override
//            public void onResponse(Call call, final Response response) throws IOException {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//
//                            String jsonData = response.body().string();
//                            JSONObject Jobject = new JSONObject(jsonData);
////                            tok = Jobject.getString("text");
////                            sovet.startAnimation(alpha_in);
////                            sovet.setText(tok);
//
//                        } catch (IOException | JSONException e) {
////                            tok = "";
////                            sovet.setText(tok);
//                        }
//                    }
//                });
//            }
//        });
//    }
}
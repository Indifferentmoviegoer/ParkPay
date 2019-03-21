package com.example.parkpay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class EditCardActivity extends AppCompatActivity {

    EditText editCodeCard;
    EditText editNameCard;
    Button editCardButton;
    String codeCard;
    String nameCard;
    Context c;
    SharedPreferences settings;
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_TOKEN ="Token";
    public static final String APP_PREFERENCES_CARD_DELETE ="cardDelete";
    public static final String APP_PREFERENCES_STATUS ="Status";
    public static final String APP_PREFERENCES_MSG ="Msg";
    private static final String TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_card);

        editCodeCard=(EditText)findViewById(R.id.editCodeCard);
        editNameCard=(EditText)findViewById(R.id.editNameCard);
        editCardButton=(Button)findViewById(R.id.editCardButton);
        settings=getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        c=this;

        editCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                codeCard=editCodeCard.getText().toString();
                nameCard=editNameCard.getText().toString();

                doPostRequest("http://192.168.252.199/card/edit");

                if(settings.contains(APP_PREFERENCES_STATUS)){

                    if(Objects.equals(settings.getString(APP_PREFERENCES_STATUS, ""), "1")){

                        Intent intent = new Intent(EditCardActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(getApplicationContext(),settings.getString(APP_PREFERENCES_MSG, ""),
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "Нажми еще раз!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void doPostRequest(String url){

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        try {
            json.put("token",settings.getString(APP_PREFERENCES_TOKEN, ""));
            json.put("card_id",settings.getString(APP_PREFERENCES_CARD_DELETE, ""));
            json.put("name",nameCard);
            json.put("code_card",codeCard);

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

                        Log.d(TAG,Jobject.getString("status"));
                        Log.d(TAG,Jobject.getString("msg"));

                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(APP_PREFERENCES_STATUS,Jobject.getString("status"));
                        editor.putString(APP_PREFERENCES_MSG,Jobject.getString("msg"));
                        editor.apply();
                    } catch (IOException | JSONException e) {
                        Toast.makeText(c,"Ошибка "+e,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}

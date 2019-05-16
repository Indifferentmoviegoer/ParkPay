package com.example.parkpay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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

    private EditText editCodeCard;
    private EditText editNameCard;
    private AppCompatButton editCardButton;
    private ImageView scanEdit;

    private String codeCard;
    private String nameCard;
    private ImageView backEdit;

    private Context c;
    private SharedPreferences settings;
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    private static final String APP_PREFERENCES = "mysettings";
    private static final String APP_PREFERENCES_TOKEN ="Token";
    private static final String APP_PREFERENCES_CARD_CODE ="cardCode";
    private static final String APP_PREFERENCES_CARD_NAME ="cardName";
    private static final String APP_PREFERENCES_STATUS ="Status";
    private static final String APP_PREFERENCES_MSG ="Msg";
    private static final String APP_PREFERENCES_CARD_DELETE ="cardDelete";
    private static final String TAG = "myLogs";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_card);

        editCodeCard= findViewById(R.id.editCodeCard);
        editNameCard= findViewById(R.id.editNameCard);
        editCardButton= findViewById(R.id.editCardButton);
        backEdit= findViewById(R.id.backEdit);
        scanEdit= findViewById(R.id.scanEdit);


        settings=getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        c=this;

        scanEdit.setOnClickListener(view -> {

            IntentIntegrator integrator = new IntentIntegrator(EditCardActivity.this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
            integrator.setPrompt("Сканирование QR кода");
            integrator.initiateScan();

        });


        if(settings.contains(APP_PREFERENCES_CARD_NAME)){
            editNameCard.setText(settings.getString(APP_PREFERENCES_CARD_NAME, ""));
        }

        backEdit.setOnClickListener(v -> {

            Intent intent = new Intent(c,
                    MainActivity.class);
            startActivity(intent);

        });

        editCardButton.setOnClickListener(v -> {

            codeCard=editCodeCard.getText().toString();
            nameCard=editNameCard.getText().toString();

            boolean checkConnection=MainActivity.isOnline(c);

                if(checkConnection) {

            doPostRequest("https://api.mobile.goldinnfish.com/card/edit");

                }
                else {
                    Toast.makeText(getApplicationContext(), "Отсутствует интернет соединение!",
                            Toast.LENGTH_SHORT).show();
                }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(settings.contains(APP_PREFERENCES_CARD_CODE)){
            editCodeCard.setText(settings.getString(APP_PREFERENCES_CARD_CODE, ""));
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if(result != null) {
            if(result.getContents() == null) {

                Log.d(TAG,"Отменено");

            } else {

                Log.d(TAG,"Результат: " + result.getContents());

                SharedPreferences.Editor editor = settings.edit();
                editor.putString(APP_PREFERENCES_CARD_CODE,result.getContents());
                editor.apply();
                Intent i = new Intent(c,
                        EditCardActivity.class);
                startActivity(i);

            }
        } else {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    private void doPostRequest(String url){

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        try {
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
                .addHeader("Authorization","Bearer "+
                        Objects.requireNonNull(settings.getString(APP_PREFERENCES_TOKEN, "")))
                .url(url)
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
                        JSONObject Jobject = new JSONObject(jsonData);

                        Log.d(TAG,Jobject.getString("status"));
                        Log.d(TAG,Jobject.getString("msg"));

                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(APP_PREFERENCES_STATUS,Jobject.getString("status"));
                        editor.putString(APP_PREFERENCES_MSG,Jobject.getString("msg"));
                        editor.apply();

                        if(settings.contains(APP_PREFERENCES_STATUS)){

                            if(Objects.equals(settings.getString(APP_PREFERENCES_STATUS, ""), "1")){

                                Toast.makeText(c,"Сохранение",Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(EditCardActivity.this,
                                        MainActivity.class);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(getApplicationContext(),settings.getString(APP_PREFERENCES_MSG, ""),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (IOException | JSONException e) {
                        Log.d(TAG,"Ошибка "+e);
                    }
                });
            }
        });
    }

}

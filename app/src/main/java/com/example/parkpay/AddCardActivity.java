package com.example.parkpay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddCardActivity extends AppCompatActivity {

    private AppCompatButton buttonAddCard;
    private EditText numberAddCard;
    private EditText nameAddCard;
    private ImageView backAdd;
    private ImageView scan;


    private Context c;
    private SharedPreferences settings;
    private String numberCard;
    private String nameCard;
    private ArrayList<String> child;

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    private static final String APP_PREFERENCES = "mysettings";
    private static final String APP_PREFERENCES_CARD ="Card";
    private static final String APP_PREFERENCES_CARDS ="Cards";
    public static final String APP_PREFERENCES_VIRTUAL_CARDS ="virtualCards";
    private static final String APP_PREFERENCES_TOKEN ="Token";
    private static final String APP_PREFERENCES_STATUS ="Status";
    private static final String APP_PREFERENCES_MSG ="Message";
    private static final String TAG = "myLogs";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        c=this;
        buttonAddCard=findViewById(R.id.buttonAddCard);
        numberAddCard=findViewById(R.id.numberAddCard);
        nameAddCard=findViewById(R.id.nameAddCard);
        backAdd=findViewById(R.id.backAdd);
        scan=findViewById(R.id.scan);

        settings= Objects.requireNonNull(c)
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        scan.setOnClickListener(view -> {

            IntentIntegrator integrator = new IntentIntegrator(AddCardActivity.this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
            integrator.setPrompt("Сканирование QR кода");
            integrator.initiateScan();
        });

        if(settings.contains(APP_PREFERENCES_CARD)){
            numberAddCard.setText(settings.getString(APP_PREFERENCES_CARD, ""));
        }

        backAdd.setOnClickListener(v -> {

            Intent intent = new Intent(c,
                    MainActivity.class);
            startActivity(intent);

        });

        buttonAddCard.setOnClickListener(v -> {

            child = new ArrayList<>();

            numberCard=numberAddCard.getText().toString();
            nameCard=nameAddCard.getText().toString();

            if(settings.contains(APP_PREFERENCES_CARDS)){

                child=MainActivity.getArrayList(APP_PREFERENCES_CARDS,settings);
            }

            if(numberCard.equals("") || numberCard.length() == 0){

                Toast.makeText(getApplicationContext(), "Заполните все поля ввода!",
                        Toast.LENGTH_SHORT).show();
            }

            else {

                if(numberCard.length() == 16&&!numberCard.contains(" ")&&
                        numberCard.matches("^[a-zA-Z0-9]+$"))
                {
                    boolean checkConnection=MainActivity.isOnline(c);

                    if(checkConnection) {

                        addCard();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Отсутствует интернет соединение!",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                else {
                    Toast.makeText(getApplicationContext(),
                            "Номер карты должен состоять только из латинских букв и цифр, длиной 16 символов!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(settings.contains(APP_PREFERENCES_CARD)){
            numberAddCard.setText(settings.getString(APP_PREFERENCES_CARD, ""));
        }
    }

    // Обрабатываем результат, полученный от приложения сканера:
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if(result != null) {
            if(result.getContents() == null) {

                Log.d(TAG,"Отменено");

            } else {

                Log.d(TAG,"Результат: " + result.getContents());

                SharedPreferences.Editor editor = settings.edit();
                editor.putString(APP_PREFERENCES_CARD,result.getContents());
                editor.apply();
                Intent i = new Intent(c,
                        AddCardActivity.class);
                startActivity(i);

            }
        } else {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    private void addCard(){

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        try {
            json.put("code_card",numberCard);
            json.put("name",nameCard);

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
                .url("https://api.mobile.goldinnfish.com/card/add")
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.v("TAG", Objects.requireNonNull(call.request().body()).toString());
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

                        Intent i = new Intent(c, MainActivity.class);


                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(APP_PREFERENCES_STATUS,Jobject.getString("status"));
                        editor.putString(APP_PREFERENCES_MSG,Jobject.getString("msg"));
                        editor.apply();

                        if(settings.contains(APP_PREFERENCES_STATUS)){

                            if(Objects.equals(settings.getString(APP_PREFERENCES_STATUS, ""), "1")){

                                Toast.makeText(c,
                                        "Добавление. Пожалуйста, подождите!",
                                        Toast.LENGTH_SHORT).show();

                                child.add(numberCard);
                                MainActivity.saveArrayList(child, APP_PREFERENCES_CARDS,settings);
                                startActivity(i);
                            }
                            else {
                                Toast.makeText(c,settings.getString(APP_PREFERENCES_MSG, ""),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (IOException | JSONException e) {

                        Log.d(TAG,"Ошибка: "+e);
                    }
                });
            }
        });
    }
}

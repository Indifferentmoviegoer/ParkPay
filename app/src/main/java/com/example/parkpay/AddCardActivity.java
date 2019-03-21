package com.example.parkpay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

    Button buttonAddCard;
    EditText numberAddCard;
    Context c;
    SharedPreferences settings;
    String numberCard;
    String nameCard="";
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_CARD ="Card";
    public static final String APP_PREFERENCES_CARDS ="Cards";
    public static final String APP_PREFERENCES_VIRTUAL_CARDS ="virtualCards";
    public static final String APP_PREFERENCES_TOKEN ="Token";
    public static final String APP_PREFERENCES_STATUS ="Status";
    private static final String TAG = "myLogs";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        c=this;
        buttonAddCard=(Button)findViewById(R.id.buttonAddCard);
        numberAddCard=(EditText)findViewById(R.id.numberAddCard);

        settings= Objects.requireNonNull(c)
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        if(settings.contains(APP_PREFERENCES_CARD)){
            numberAddCard.setText(settings.getString(APP_PREFERENCES_CARD, ""));
        }

        buttonAddCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(c, MainActivity.class);
                ArrayList<String> child = new ArrayList<String>();


                numberCard=numberAddCard.getText().toString();

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

                            doPostRequest("http://192.168.252.199/card/add");

                            child.add(numberCard);
                            MainActivity.saveArrayList(child, APP_PREFERENCES_CARDS,settings);
                            startActivity(i);
                        }
                        else {
                            Toast.makeText(getApplicationContext(),
                                    "Номер карты должен состоять только из латинских букв и цифр, длиной 16 символов!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
            }
        });

        numberAddCard.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (numberAddCard.getRight() - numberAddCard.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        scanQR(v);

                        return true;
                    }
                }
                return false;
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

    // Запускаемм сканер штрих кода:
    public void scanBar(View v) {
        try {
            // Запускаем переход на com.google.zxing.client.android.SCAN с помощью intent:
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {

            // Предлагаем загрузить с Play Market:
            showDialog(AddCardActivity.this, "Сканнер не найден", "Установить сканер с Play Market?", "Да", "Нет").show();
        }
    }

    // Запуск сканера qr-кода:
    public void scanQR(View v) {
        try {
            // Запускаем переход на com.google.zxing.client.android.SCAN с помощью intent:
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            // Предлагаем загрузить с Play Market:
            showDialog(AddCardActivity.this, "Сканнер не найден", "Установить сканер с Play Market?", "Да", "Нет").show();
        }
    }

    // alert dialog для перехода к загрузке приложения сканера:
    private static AlertDialog showDialog(final Activity act, CharSequence title,
                                          CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

                // Ссылка поискового запроса для загрузки приложения:
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }

    // Обрабатываем результат, полученный от приложения сканера:
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                // Получаем данные после работы сканера и выводим их в Toast сообщении:
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(APP_PREFERENCES_CARD,contents);
                editor.apply();
                // numberCard.setText(contents);
//                Toast toast = Toast.makeText(c, "Содержание: " + contents + " Формат: " + format, Toast.LENGTH_LONG);
//                toast.show();
                Intent i = new Intent(c,
                        AddCardActivity.class);
                startActivity(i);
            }
        }
    }
    public void doPostRequest(String url){

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        try {
            json.put("token",settings.getString(APP_PREFERENCES_TOKEN, ""));
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
                        editor.apply();
                    } catch (IOException | JSONException e) {
                        Toast.makeText(c,"Ошибка "+e,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}

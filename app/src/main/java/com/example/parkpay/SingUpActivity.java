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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

//import static com.example.parkpay.QRActivity.ACTION_SCAN;

public class SingUpActivity extends AppCompatActivity {
    Button signUp;
    EditText name;
    EditText fam;
    EditText ot;
    EditText phone;
    EditText email;
    EditText pass;
    EditText confirmPassword;
    Context c;
    //static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_NAME ="Name";
    public static final String APP_PREFERENCES_FAM="Fam";
    public static final String APP_PREFERENCES_OTCH ="Otch";
    public static final String APP_PREFERENCES_NUMBER ="Number";
    public static final String APP_PREFERENCES_MAIL ="Email";
    public static final String APP_PREFERENCES_PASS ="Password";
    public static final String APP_PREFERENCES_TOKEN ="Token";
    //public static final String APP_PREFERENCES_CARD ="Card";

    SharedPreferences settings;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singn_up);

        signUp= findViewById(R.id.signUp);
        name= findViewById(R.id.name);
        fam= findViewById(R.id.fam);
        ot= findViewById(R.id.otc);
        phone= findViewById(R.id.number);
        email= findViewById(R.id.mail);
        pass= findViewById(R.id.passw);
        confirmPassword= findViewById(R.id.confirmPassword);
        c=this;
        settings=getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);


//        numberCard.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                final int DRAWABLE_LEFT = 0;
//                final int DRAWABLE_TOP = 1;
//                final int DRAWABLE_RIGHT = 2;
//                final int DRAWABLE_BOTTOM = 3;
//
//                if(event.getAction() == MotionEvent.ACTION_UP) {
//                    if(event.getRawX() >= (numberCard.getRight() - numberCard.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
//                        scanQR(v);
//                        return true;
//                    }
//                }
//                return false;
//            }
//        });

        signUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (name.getText().toString().equals("")||name.getText().length() == 0||
                    fam.getText().toString().equals("")||fam.getText().length() == 0||
                    ot.getText().toString().equals("")||ot.getText().length() == 0||
                    phone.getText().toString().equals("")||phone.getText().length() == 0||
                    email.getText().toString().equals("")||email.getText().length() == 0||
                    pass.getText().toString().equals("")||pass.getText().length() == 0||
                    confirmPassword.getText().toString().equals("")||confirmPassword.getText().length()==0)
                {
                    Toast.makeText(getApplicationContext(),"Заполните все поля ввода!",
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (pass.getText().toString().equals(confirmPassword.getText().toString())){
                        Intent intent = new Intent(c,
                                MainActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Пароли не совпадают",
                                Toast.LENGTH_SHORT).show();
                    }
                    doGetRequest("https://www.mobile.ru/register");
                    if(settings.contains(APP_PREFERENCES_TOKEN)){
                        Intent intent = new Intent(c,
                                MainActivity.class);
                        startActivity(intent);
                    }
                    if(!settings.contains(APP_PREFERENCES_TOKEN)) {
                        Toast.makeText(getApplicationContext(), "Неверный логин или пароль!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(APP_PREFERENCES_NAME,name.getText().toString());
        editor.putString(APP_PREFERENCES_FAM,fam.getText().toString());
        editor.putString(APP_PREFERENCES_OTCH,ot.getText().toString());
        editor.putString(APP_PREFERENCES_NUMBER,phone.getText().toString());
        editor.putString(APP_PREFERENCES_MAIL,email.getText().toString());
        editor.putString(APP_PREFERENCES_PASS,pass.getText().toString());
        editor.apply();
    }

    private void doGetRequest(String url){
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject json = new JSONObject();
        try {
            //json.put("login",login);
            json.put("password",pass);
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
                        editor.putString(APP_PREFERENCES_TOKEN,Jobject.getString("text"));
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

//    // Запускаемм сканер штрих кода:
//    public void scanBar(View v) {
//        try {
//
//            // Запускаем переход на com.google.zxing.client.android.SCAN с помощью intent:
//            Intent intent = new Intent(ACTION_SCAN);
//            intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
//            startActivityForResult(intent, 0);
//        } catch (ActivityNotFoundException anfe) {
//
//            // Предлагаем загрузить с Play Market:
//            showDialog(SingUpActivity.this, "Сканнер не найден", "Установить сканер с Play Market?", "Да", "Нет").show();
//        }
//    }
//
//    // Запуск сканера qr-кода:
//    public void scanQR(View v) {
//        try {
//
//            // Запускаем переход на com.google.zxing.client.android.SCAN с помощью intent:
//            Intent intent = new Intent(ACTION_SCAN);
//            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
//            startActivityForResult(intent, 0);
//        } catch (ActivityNotFoundException anfe) {
//
//            // Предлагаем загрузить с Play Market:
//            showDialog(SingUpActivity.this, "Сканнер не найден", "Установить сканер с Play Market?", "Да", "Нет").show();
//        }
//    }
//
//    // alert dialog для перехода к загрузке приложения сканера:
//    private static AlertDialog showDialog(final Activity act, CharSequence title,
//                                          CharSequence message,CharSequence buttonYes, CharSequence buttonNo) {
//        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
//        downloadDialog.setTitle(title);
//        downloadDialog.setMessage(message);
//        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//                // Ссылка поискового запроса для загрузки приложения:
//                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                try {
//                    act.startActivity(intent);
//                } catch (ActivityNotFoundException anfe) {
//
//                }
//            }
//        });
//        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialogInterface, int i) {
//            }
//        });
//        return downloadDialog.show();
//    }
//
//    // Обрабатываем результат, полученный от приложения сканера:
//    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        if (requestCode == 0) {
//            if (resultCode == RESULT_OK) {
//
//                // Получаем данные после работы сканера и выводим их в Toast сообщении:
//                String contents = intent.getStringExtra("SCAN_RESULT");
//                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
//                numberCard.setText(contents);
////                Toast toast = Toast.makeText(this, "Содержание: " + contents + " Формат: " + format, Toast.LENGTH_LONG);
////                toast.show();
//
//            }
//        }
//    }
}

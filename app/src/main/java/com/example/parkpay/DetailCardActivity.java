package com.example.parkpay;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DetailCardActivity extends AppCompatActivity {

    TextView numberCard;
    String number;
    Button deleteCard;
    Button payCard;
    String[] childAttribute;
    Context c;
    SharedPreferences settings;
    int groupPosition;
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_CARDS ="Cards";
    public static final String APP_PREFERENCES_VIRTUAL_CARDS ="virtualCards";
    public static final String APP_PREFERENCES_TOKEN ="Token";
    public static final String APP_PREFERENCES_STATUS ="Status";
    private static final String TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_card);

        c=this;
        numberCard=(TextView)findViewById(R.id.numberCard) ;
        deleteCard=(Button)findViewById(R.id.deleteCard);
        payCard=(Button)findViewById(R.id.payCard);
        settings= Objects.requireNonNull(c)
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                number= null;
                groupPosition= 10;
            } else {
                number= extras.getString("POSITION_CARD");
                groupPosition= extras.getInt("POSITION_GROUP");
            }
        } else {
            number= (String) savedInstanceState.getSerializable("POSITION_CARD");
            groupPosition= (int) savedInstanceState.getSerializable("POSITION_GROUP");
        }
        if(number!=null){
            numberCard.setText(number);
        }

        deleteCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(number!=null){

                    AlertDialog.Builder builder = new AlertDialog.Builder(c);
                    builder.setMessage(Html
                            .fromHtml("<font color='#000000'>Вы действительно хотите удалить данную карту?</font>"));
                    builder.setCancelable(false);
                    builder.setPositiveButton("Да",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    Intent i = new Intent(c, MainActivity.class);

                                    ArrayList<String> children1 = new ArrayList<String>();
                                    ArrayList<String> children2 = new ArrayList<String>();

                                    if(groupPosition==0){
                                        if(settings.contains(APP_PREFERENCES_CARDS)){
                                            children1=MainActivity.getArrayList(APP_PREFERENCES_CARDS,settings);
                                        }
                                        childAttribute=new String[] {"1",number,"2"};

                                        doPostRequest("http://192.168.252.199/card/delete");

                                        children1.remove(childAttribute);

                                        MainActivity.saveArrayList(children1, APP_PREFERENCES_CARDS,settings);
                                        startActivity(i);
                                    }
                                    if(groupPosition==1){
                                        if(settings.contains(APP_PREFERENCES_VIRTUAL_CARDS)){
                                            children2=MainActivity.getArrayList(APP_PREFERENCES_VIRTUAL_CARDS,settings);
                                        }
                                        childAttribute=new String[] {"1",number,"2"};

                                        children2.remove(childAttribute);

                                        MainActivity.saveArrayList(children2, APP_PREFERENCES_VIRTUAL_CARDS,settings);
                                        startActivity(i);
                                    }
                                }
                            });
                    builder.setNegativeButton("Нет",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });

        payCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(c,
                        PayActivity.class);
                startActivity(intent);
            }
        });
    }

    public void doPostRequest(String url){

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        try {
            json.put("token",settings.getString(APP_PREFERENCES_TOKEN, ""));
            json.put("card_id","261");

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

//    public void doGetRequest(String url){
//
//        OkHttpClient client = new OkHttpClient();
//
//        HttpUrl mySearchUrl = new HttpUrl.Builder()
//                .scheme("http")
//                .host("192.168.252.199")
//                .addPathSegment("user")
//                .addPathSegment("get_info")
//                .addQueryParameter("token", settings.getString(APP_PREFERENCES_TOKEN, ""))
//                .build();
//
//        Log.d(TAG,mySearchUrl.toString());
//
//        final Request request = new Request.Builder()
//                .url(mySearchUrl)
//                .addHeader("Content-Type", "application/json; charset=utf-8")
//                .method("GET", null)
//                .build();
//        Call call = client.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                Log.v("TAG", Objects.requireNonNull(call.request().body()).toString());
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                    }
//                });
//            }
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
//                runOnUiThread(() -> {
//                    try {
//
//                        String jsonData = null;
//                        if (response.body() != null) {
//                            jsonData = response.body().string();
//                        }
//
//                        JSONObject parentObject = new JSONObject(jsonData);
//                        JSONObject Jobject = parentObject.getJSONObject("user");
//
//                        Log.d(TAG,Jobject.getString("card_id"));
//                        Log.d(TAG,Jobject.getString("name"));
//                        Log.d(TAG,Jobject.getString("code"));
//
//                        SharedPreferences.Editor editor = settings.edit();
//                        editor.putString(APP_PREFERENCES_NAME,Jobject.getString("card_id"));
//                        editor.putString(APP_PREFERENCES_MAIL,Jobject.getString("name"));
//                        editor.putString(APP_PREFERENCES_NUMBER,Jobject.getString("code"));
//                        editor.apply();
//
//                    } catch (IOException | JSONException e) {
//                        Log.d(TAG,"Ошибка "+e);
//                    }
//                });
//            }
//        });
//    }

}

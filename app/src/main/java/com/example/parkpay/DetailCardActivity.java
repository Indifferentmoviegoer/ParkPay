package com.example.parkpay;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
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
    TextView moneyCard;
    TextView moneyTitleCard;
    TextView bonusCard;
    TextView bonusTitleCard;
    TextView titleDetailCard;
    String cardNumber="";
    String number;
    Button deleteCard;
    Button payCard;
    Button editCard;
    ImageView updateCard;
    ImageView imageCard;
    ImageView closeDetail;
    Context c;
    ProgressBar progressBarCard;

    SharedPreferences settings;
    ArrayList<String> child = new ArrayList<String>();
    ArrayList<String> children2 = new ArrayList<String>();
    int groupPosition=10;
    private Toolbar toolbar;
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_CARDS ="Cards";
    public static final String APP_PREFERENCES_VIRTUAL_CARDS ="virtualCards";
    public static final String APP_PREFERENCES_TOKEN ="Token";
    public static final String APP_PREFERENCES_STATUS ="Status";
    public static final String APP_PREFERENCES_CARD_DELETE ="cardDelete";
    public static final String APP_PREFERENCES_CARD_NAME ="cardName";
    public static final String APP_PREFERENCES_CARD_CODE ="cardCode";
    public static final String APP_PREFERENCES_MONEY ="money";
    public static final String APP_PREFERENCES_BONUS ="bonus";
    public static final String APP_PREFERENCES_POSITION_CARD ="position";
    public static final String APP_PREFERENCES_POSITION_GROUP ="group";
    private static final String TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_card);

        c=this;
        numberCard=(TextView)findViewById(R.id.numberCard);
        moneyCard=(TextView)findViewById(R.id.moneyCard);
        moneyTitleCard=(TextView)findViewById(R.id.moneyTitleCard);
        bonusCard=(TextView)findViewById(R.id.bonusCard);
        bonusTitleCard=(TextView)findViewById(R.id.bonusTitleCard);
        titleDetailCard=(TextView)findViewById(R.id.titleDetailCard);
        payCard=(Button)findViewById(R.id.payCard);
        deleteCard=(Button)findViewById(R.id.deleteCard);
        editCard=(Button)findViewById(R.id.editCard);
        updateCard=(ImageView)findViewById(R.id.updateCard);
        imageCard=(ImageView)findViewById(R.id.imageCard);
        closeDetail=(ImageView)findViewById(R.id.closeDetail);
        progressBarCard=(ProgressBar) findViewById(R.id.progressBarCard);

        numberCard.setVisibility(View.INVISIBLE);
        moneyCard.setVisibility(View.INVISIBLE);
        moneyTitleCard.setVisibility(View.INVISIBLE);
        bonusCard.setVisibility(View.INVISIBLE);
        bonusTitleCard.setVisibility(View.INVISIBLE);
        titleDetailCard.setVisibility(View.INVISIBLE);
        payCard.setVisibility(View.INVISIBLE);
        deleteCard.setVisibility(View.INVISIBLE);
        editCard.setVisibility(View.INVISIBLE);
        updateCard.setVisibility(View.INVISIBLE);
        imageCard.setVisibility(View.INVISIBLE);
        closeDetail.setVisibility(View.INVISIBLE);



        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        settings= Objects.requireNonNull(c)
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        number=settings.getString(APP_PREFERENCES_POSITION_CARD,"");
        groupPosition=settings.getInt(APP_PREFERENCES_POSITION_GROUP,0);
        numberCard.setText(number);
        cardNumber=numberCard.getText().toString();

        boolean checkConnection=MainActivity.isOnline(c);

//        if(checkConnection) {

        doGetRequest();

//        }
//        else {
//            Toast.makeText(getApplicationContext(), "Отсутствует интернет соединение!",
//                    Toast.LENGTH_SHORT).show();
//        }

        closeDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(c,
                        MainActivity.class);
                startActivity(intent);

            }
        });


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

                                    if(groupPosition==0){

                                        if(settings.contains(APP_PREFERENCES_CARDS)){
                                            child=MainActivity.getArrayList(APP_PREFERENCES_CARDS,settings);
                                        }

                                        boolean checkConnection=MainActivity.isOnline(c);

//                                        if(checkConnection) {

                                        doPostRequest("http://192.168.252.199/card/delete");

//                                        }
//                                        else {
//                                            Toast.makeText(getApplicationContext(), "Отсутствует интернет соединение!",
//                                                    Toast.LENGTH_SHORT).show();
//                                        }
                                    }
                                    if(groupPosition==1){

                                        if(settings.contains(APP_PREFERENCES_VIRTUAL_CARDS)){

                                            children2=MainActivity.getArrayList(APP_PREFERENCES_VIRTUAL_CARDS,settings);
                                        }

                                        children2.remove(cardNumber);

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

        editCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(c,
                        EditCardActivity.class);
                startActivity(intent);
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

        updateCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Toast.makeText(c,"Обновление",Toast.LENGTH_SHORT).show();

                progressBarCard.setVisibility(View.VISIBLE);
                numberCard.setVisibility(View.INVISIBLE);
                moneyCard.setVisibility(View.INVISIBLE);
                moneyTitleCard.setVisibility(View.INVISIBLE);
                bonusCard.setVisibility(View.INVISIBLE);
                bonusTitleCard.setVisibility(View.INVISIBLE);
                titleDetailCard.setVisibility(View.INVISIBLE);
                payCard.setVisibility(View.INVISIBLE);
                deleteCard.setVisibility(View.INVISIBLE);
                editCard.setVisibility(View.INVISIBLE);
                updateCard.setVisibility(View.INVISIBLE);
                imageCard.setVisibility(View.INVISIBLE);
                closeDetail.setVisibility(View.INVISIBLE);

                doGetRequest();

            }
        });

    }

    public void doPostRequestCardInfo(String url){

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        try {
            json.put("token",settings.getString(APP_PREFERENCES_TOKEN, ""));
            json.put("card_id",settings.getString(APP_PREFERENCES_CARD_DELETE, ""));

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

                if(call.request().body()!=null)
                {
                    Log.d(TAG, Objects.requireNonNull(call.request().body()).toString());
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        progressBarCard.setVisibility(View.INVISIBLE);
                        numberCard.setVisibility(View.VISIBLE);
                        moneyCard.setVisibility(View.VISIBLE);
                        moneyTitleCard.setVisibility(View.VISIBLE);
                        bonusCard.setVisibility(View.VISIBLE);
                        bonusTitleCard.setVisibility(View.VISIBLE);
                        titleDetailCard.setVisibility(View.VISIBLE);
                        payCard.setVisibility(View.VISIBLE);
                        deleteCard.setVisibility(View.VISIBLE);
                        editCard.setVisibility(View.VISIBLE);
                        updateCard.setVisibility(View.VISIBLE);
                        imageCard.setVisibility(View.VISIBLE);
                        closeDetail.setVisibility(View.VISIBLE);

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

                        JSONArray jsonArray = new JSONArray(jsonData);


                        JSONObject Jobject = jsonArray.getJSONObject(0);

                        Log.d(TAG,Jobject.getString("code"));
                        Log.d(TAG,Jobject.getString("balance_money"));
                        Log.d(TAG,Jobject.getString("balance_bonus"));

                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(APP_PREFERENCES_MONEY,Jobject.getString("balance_money"));
                        editor.putString(APP_PREFERENCES_BONUS,Jobject.getString("balance_bonus"));
                        editor.apply();

                        moneyCard.setText(settings.getString(APP_PREFERENCES_MONEY, ""));
                        bonusCard.setText(settings.getString(APP_PREFERENCES_BONUS, ""));

                        progressBarCard.setVisibility(View.INVISIBLE);
                        numberCard.setVisibility(View.VISIBLE);
                        moneyCard.setVisibility(View.VISIBLE);
                        moneyTitleCard.setVisibility(View.VISIBLE);
                        bonusCard.setVisibility(View.VISIBLE);
                        bonusTitleCard.setVisibility(View.VISIBLE);
                        titleDetailCard.setVisibility(View.VISIBLE);
                        payCard.setVisibility(View.VISIBLE);
                        deleteCard.setVisibility(View.VISIBLE);
                        editCard.setVisibility(View.VISIBLE);
                        updateCard.setVisibility(View.VISIBLE);
                        imageCard.setVisibility(View.VISIBLE);
                        closeDetail.setVisibility(View.VISIBLE);

                    } catch (IOException | JSONException e) {

                        Log.d(TAG,"Ошибка "+e);

                        progressBarCard.setVisibility(View.INVISIBLE);
                        numberCard.setVisibility(View.VISIBLE);
                        moneyCard.setVisibility(View.VISIBLE);
                        moneyTitleCard.setVisibility(View.VISIBLE);
                        bonusCard.setVisibility(View.VISIBLE);
                        bonusTitleCard.setVisibility(View.VISIBLE);
                        titleDetailCard.setVisibility(View.VISIBLE);
                        payCard.setVisibility(View.VISIBLE);
                        deleteCard.setVisibility(View.VISIBLE);
                        editCard.setVisibility(View.VISIBLE);
                        updateCard.setVisibility(View.VISIBLE);
                        imageCard.setVisibility(View.VISIBLE);
                        closeDetail.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    public void doPostRequest(String url){

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        try {
            json.put("token",settings.getString(APP_PREFERENCES_TOKEN, ""));
            json.put("card_id",settings.getString(APP_PREFERENCES_CARD_DELETE, ""));

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

                if(call.request().body()!=null)
                {
                    Log.d(TAG, Objects.requireNonNull(call.request().body()).toString());
                }

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

                        Intent i = new Intent(c, MainActivity.class);

                        Log.d(TAG,Jobject.getString("status"));
                        Log.d(TAG,Jobject.getString("msg"));

                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(APP_PREFERENCES_STATUS,Jobject.getString("status"));
                        editor.apply();

                        if(Objects.equals(settings.getString(APP_PREFERENCES_STATUS,""), "1"))

                        {
                            child.remove(cardNumber);

                            Toast
                                    .makeText(c,"Удаление карты",Toast.LENGTH_SHORT)
                                    .show();

                            MainActivity.saveArrayList(child, APP_PREFERENCES_CARDS,settings);
                            startActivity(i);
                        }

                        if(Objects.equals(settings.getString(APP_PREFERENCES_STATUS,""), "0"))
                        {
                            Toast
                                    .makeText(c,Jobject.getString("msg"),Toast.LENGTH_SHORT)
                                    .show();
                        }

                    } catch (IOException | JSONException e) {
                        Toast.makeText(c,"Ошибка "+e,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void doGetRequest(){

        OkHttpClient client = new OkHttpClient();

        HttpUrl mySearchUrl = new HttpUrl.Builder()
                .scheme("http")
                .host("192.168.252.199")
                .addPathSegment("card")
                .addPathSegment("list")
                .addQueryParameter("token", settings.getString(APP_PREFERENCES_TOKEN, ""))
                .build();

        Log.d(TAG,mySearchUrl.toString());

        final Request request = new Request.Builder()
                .url(mySearchUrl)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .method("GET", null)
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
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                runOnUiThread(() -> {
                    try {

                        String jsonData = null;
                        if (response.body() != null) {
                            jsonData = response.body().string();
                        }

                        JSONArray jsonArray = new JSONArray(jsonData);

                        child = new ArrayList<String>();

                        for(int i=0;i<jsonArray.length();i++){

                            JSONObject Jobject = jsonArray.getJSONObject(i);

                            if(Jobject.getString("code").contains(cardNumber)){

                                Log.d(TAG,Jobject.getString("card_id"));
                                Log.d(TAG,Jobject.getString("name"));
                                Log.d(TAG,Jobject.getString("code"));

                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString(APP_PREFERENCES_CARD_DELETE,Jobject.getString("card_id"));
                                editor.putString(APP_PREFERENCES_CARD_NAME,Jobject.getString("name"));
                                editor.putString(APP_PREFERENCES_CARD_CODE,Jobject.getString("code"));
                                editor.apply();

                                if(settings.contains(APP_PREFERENCES_CARD_DELETE)&&
                                        settings.contains(APP_PREFERENCES_CARD_NAME)
                                        &&settings.contains(APP_PREFERENCES_CARD_CODE)){

                                    titleDetailCard.setText(settings.getString(APP_PREFERENCES_CARD_NAME, ""));
                                    numberCard.setText(settings.getString(APP_PREFERENCES_CARD_CODE, ""));

                                    doPostRequestCardInfo("http://192.168.252.199/card/get_info");


                                }

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

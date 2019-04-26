package com.example.parkpay;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
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
    ListView history;
    TextView nameCard;
    String cardNumber="";
    //Button deleteCard;
    Button payCard;
    //Button editCard;
    ImageView updateCard;
    ImageView imageCard;
    ImageView imageDelete;
    ImageView imageEdit;
    ImageView closeDetail;
    Context c;
    ProgressBar progressBarCard;

    ArrayList<String> nameOperation;
    ArrayList<String> dateOperation;
    ArrayList<String> valueOperation;

    ArrayList<String> operationName;
    ArrayList<String> operationDate;
    ArrayList<String> operationValue;

    HistoryAdapter historyAdapter;

    SharedPreferences settings;
    ArrayList<String> child = new ArrayList<String>();
    ArrayList<String> children2 = new ArrayList<String>();

    //int groupPosition=10;
    private Toolbar toolbar;
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_CARDS ="Cards";
    public static final String APP_PREFERENCES_TOKEN ="Token";
    public static final String APP_PREFERENCES_STATUS ="Status";
    public static final String APP_PREFERENCES_CARD_DELETE ="cardDelete";
    public static final String APP_PREFERENCES_CARD_NAME ="cardName";
    public static final String APP_PREFERENCES_CARD_CODE ="cardCode";
    public static final String APP_PREFERENCES_MONEY ="money";
    public static final String APP_PREFERENCES_BONUS ="bonus";
    public static final String APP_PREFERENCES_POSITION_CARD ="position";
    public static final String APP_PREFERENCES_POSITION_GROUP ="group";
    public static final String APP_PREFERENCES_NAMES_CARDS ="namesCards";
    public static final String APP_PREFERENCES_NAME_OPERATION ="nameOperation";
    public static final String APP_PREFERENCES_DATE_OPERATION ="dateOperation";
    public static final String APP_PREFERENCES_VALUE_OPERATION ="valueOperation";
    private static final String TAG = "myLogs";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_card);

        StrictMode.ThreadPolicy mypolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(mypolicy);

        c=this;
        numberCard=(TextView)findViewById(R.id.numberCard);
        moneyCard=(TextView)findViewById(R.id.moneyCard);
        moneyTitleCard=(TextView)findViewById(R.id.moneyTitleCard);
        bonusCard=(TextView)findViewById(R.id.bonusCard);
        bonusTitleCard=(TextView)findViewById(R.id.bonusTitleCard);
        titleDetailCard=(TextView)findViewById(R.id.titleDetailCard);
        nameCard=(TextView)findViewById(R.id.nameCard);
        history=(ListView)findViewById(R.id.historyList);
        payCard=(Button)findViewById(R.id.payCard);
        imageDelete=(ImageView)findViewById(R.id.imageDelete);
        imageEdit=(ImageView) findViewById(R.id.imageEdit);
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
        nameCard.setVisibility(View.INVISIBLE);
        history.setVisibility(View.INVISIBLE);
        payCard.setVisibility(View.INVISIBLE);
        imageDelete.setVisibility(View.INVISIBLE);
        imageEdit.setVisibility(View.INVISIBLE);
        updateCard.setVisibility(View.INVISIBLE);
        imageCard.setVisibility(View.INVISIBLE);
        closeDetail.setVisibility(View.INVISIBLE);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setListViewHeightBasedOnChildren(history);

        // Setting on Touch Listener for handling the touch inside ScrollView
        history.setOnTouchListener((v, event) -> {
            // Disallow the touch request for parent scroll on touch of child view
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

//        View header = getLayoutInflater().inflate(R.layout.header, null);
//        View footer = getLayoutInflater().inflate(R.layout.footer, null);
//        history.addHeaderView(header);
//        history.addFooterView(footer);

        settings= Objects.requireNonNull(c)
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        //Создаем набор данных для адаптера
        nameOperation= new ArrayList<String>();
        dateOperation= new ArrayList<String>();
        valueOperation = new ArrayList<String>();

        operationName= new ArrayList<String>();
        operationDate= new ArrayList<String>();
        operationValue= new ArrayList<String>();

//        history.invalidateViews();

//        if(settings.contains(APP_PREFERENCES_NAME_OPERATION)){
//
//            nameOperation=MainActivity.getArrayList(APP_PREFERENCES_NAME_OPERATION,settings);
//        }
//
//        if(settings.contains(APP_PREFERENCES_DATE_OPERATION)){
//
//            dateOperation=MainActivity.getArrayList(APP_PREFERENCES_DATE_OPERATION,settings);
//        }
//
//        if(settings.contains(APP_PREFERENCES_VALUE_OPERATION)){
//
//            valueOperation=MainActivity.getArrayList(APP_PREFERENCES_VALUE_OPERATION,settings);
//        }

        //historyAdapter = new HistoryAdapter(c, nameOperation, dateOperation,valueOperation);
        //history.setAdapter(historyAdapter);

        cardNumber=settings.getString(APP_PREFERENCES_POSITION_CARD,"");
        //groupPosition=settings.getInt(APP_PREFERENCES_POSITION_GROUP,0);
        numberCard.setText(cardNumber);

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


        imageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(cardNumber!=null){

                    AlertDialog.Builder builder = new AlertDialog.Builder(c);
                    builder.setMessage(Html
                            .fromHtml("<font color='#000000'>Вы действительно хотите удалить данную карту?</font>"));
                    builder.setCancelable(false);
                    builder.setPositiveButton("Да",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    Intent i = new Intent(c, MainActivity.class);

//                                    if(groupPosition==0){

                                    if(settings.contains(APP_PREFERENCES_CARDS)){
                                        child=MainActivity.getArrayList(APP_PREFERENCES_CARDS,settings);
                                        children2=MainActivity.getArrayList(APP_PREFERENCES_NAMES_CARDS,settings);
                                    }

                                    boolean checkConnection=MainActivity.isOnline(c);

//                                        if(checkConnection) {

                                    doPostRequest("http://192.168.252.199/card/delete");

//                                        }
//                                        else {
//                                            Toast.makeText(getApplicationContext(), "Отсутствует интернет соединение!",
//                                                    Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                                    if(groupPosition==1){
//
//                                        if(settings.contains(APP_PREFERENCES_VIRTUAL_CARDS)){
//
//                                            children2=MainActivity.getArrayList(APP_PREFERENCES_VIRTUAL_CARDS,settings);
//                                        }
//
//                                        children2.remove(cardNumber);
//
//                                        MainActivity.saveArrayList(children2, APP_PREFERENCES_VIRTUAL_CARDS,settings);
//                                        startActivity(i);
//                                    }
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

        imageEdit.setOnClickListener(new View.OnClickListener() {
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
                nameCard.setVisibility(View.INVISIBLE);
                history.setVisibility(View.INVISIBLE);
                payCard.setVisibility(View.INVISIBLE);
                imageDelete.setVisibility(View.INVISIBLE);
                imageEdit.setVisibility(View.INVISIBLE);
                updateCard.setVisibility(View.INVISIBLE);
                imageCard.setVisibility(View.INVISIBLE);
                closeDetail.setVisibility(View.INVISIBLE);

                doGetRequest();
                getHistory("http://192.168.252.199/card/history");

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
//                        progressBarCard.setVisibility(View.INVISIBLE);
//                        numberCard.setVisibility(View.VISIBLE);
//                        moneyCard.setVisibility(View.VISIBLE);
//                        moneyTitleCard.setVisibility(View.VISIBLE);
//                        bonusCard.setVisibility(View.VISIBLE);
//                        bonusTitleCard.setVisibility(View.VISIBLE);
//                        titleDetailCard.setVisibility(View.VISIBLE);
//                        nameCard.setVisibility(View.VISIBLE);
//                        history.setVisibility(View.VISIBLE);
//                        payCard.setVisibility(View.VISIBLE);
//                        imageDelete.setVisibility(View.VISIBLE);
//                        imageEdit.setVisibility(View.VISIBLE);
//                        updateCard.setVisibility(View.VISIBLE);
//                        imageCard.setVisibility(View.VISIBLE);
//                        closeDetail.setVisibility(View.VISIBLE);
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

                        getHistory("http://192.168.252.199/card/history");

//                        progressBarCard.setVisibility(View.INVISIBLE);
//                        numberCard.setVisibility(View.VISIBLE);
//                        moneyCard.setVisibility(View.VISIBLE);
//                        moneyTitleCard.setVisibility(View.VISIBLE);
//                        bonusCard.setVisibility(View.VISIBLE);
//                        bonusTitleCard.setVisibility(View.VISIBLE);
//                        titleDetailCard.setVisibility(View.VISIBLE);
//                        nameCard.setVisibility(View.VISIBLE);
//                        history.setVisibility(View.VISIBLE);
//                        payCard.setVisibility(View.VISIBLE);
//                        imageDelete.setVisibility(View.VISIBLE);
//                        imageEdit.setVisibility(View.VISIBLE);
//                        updateCard.setVisibility(View.VISIBLE);
//                        imageCard.setVisibility(View.VISIBLE);
//                        closeDetail.setVisibility(View.VISIBLE);
                    } catch (IOException | JSONException e) {

                        Log.d(TAG,"Ошибка "+e);

//                        progressBarCard.setVisibility(View.INVISIBLE);
//                        numberCard.setVisibility(View.VISIBLE);
//                        moneyCard.setVisibility(View.VISIBLE);
//                        moneyTitleCard.setVisibility(View.VISIBLE);
//                        bonusCard.setVisibility(View.VISIBLE);
//                        bonusTitleCard.setVisibility(View.VISIBLE);
//                        titleDetailCard.setVisibility(View.VISIBLE);
//                        nameCard.setVisibility(View.VISIBLE);
//                        history.setVisibility(View.VISIBLE);
//                        payCard.setVisibility(View.VISIBLE);
//                        imageDelete.setVisibility(View.VISIBLE);
//                        imageEdit.setVisibility(View.VISIBLE);
//                        updateCard.setVisibility(View.VISIBLE);
//                        imageCard.setVisibility(View.VISIBLE);
//                        closeDetail.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    public void doPostRequest(String url){

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        try {
            json.put("card_id",settings.getString(APP_PREFERENCES_CARD_DELETE, ""));

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
                            child.remove(settings.getInt(APP_PREFERENCES_POSITION_GROUP,0));
                            children2.remove(settings.getInt(APP_PREFERENCES_POSITION_GROUP,0));

                            Toast
                                    .makeText(c,"Удаление карты",Toast.LENGTH_SHORT)
                                    .show();

                            MainActivity.saveArrayList(child, APP_PREFERENCES_CARDS,settings);
                            MainActivity.saveArrayList(children2, APP_PREFERENCES_NAMES_CARDS,settings);

                            startActivity(i);
                        }

                        if(Objects.equals(settings.getString(APP_PREFERENCES_STATUS, ""), "0")) {
                            Toast.makeText(c,
                                    Jobject.getString("msg"),
                                    Toast.LENGTH_SHORT).show();
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
                .build();

        Log.d(TAG,mySearchUrl.toString());

        final Request request = new Request.Builder()
                .url(mySearchUrl)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("Authorization","Bearer "+
                        Objects.requireNonNull(settings.getString(APP_PREFERENCES_TOKEN, "")))
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
                                    nameCard.setText(settings.getString(APP_PREFERENCES_CARD_NAME, ""));
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

    public void getHistory(String url) {

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        try {
            json.put("code_card", settings.getString(APP_PREFERENCES_CARD_CODE, ""));

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

                if (call.request().body() != null) {
                    Log.d(TAG, Objects.requireNonNull(call.request().body()).toString());
                }

                runOnUiThread(() -> {

                    progressBarCard.setVisibility(View.INVISIBLE);
                    numberCard.setVisibility(View.VISIBLE);
                    moneyCard.setVisibility(View.VISIBLE);
                    moneyTitleCard.setVisibility(View.VISIBLE);
                    bonusCard.setVisibility(View.VISIBLE);
                    bonusTitleCard.setVisibility(View.VISIBLE);
                    titleDetailCard.setVisibility(View.VISIBLE);
                    nameCard.setVisibility(View.VISIBLE);
                    history.setVisibility(View.VISIBLE);
                    payCard.setVisibility(View.VISIBLE);
                    imageDelete.setVisibility(View.VISIBLE);
                    imageEdit.setVisibility(View.VISIBLE);
                    updateCard.setVisibility(View.VISIBLE);
                    imageCard.setVisibility(View.VISIBLE);
                    closeDetail.setVisibility(View.VISIBLE);
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

                        history.invalidateViews();

                        operationName.clear();
                        operationDate.clear();
                        operationValue.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject Jobject = jsonArray.getJSONObject(i);

                            Log.d(TAG, Jobject.getString("name"));
                            Log.d(TAG, Jobject.getString("date"));
                            Log.d(TAG, Jobject.getString("value"));

                            operationName.add(Jobject.getString("name"));
                            operationDate.add(Jobject.getString("date"));
                            operationValue.add(Jobject.getString("value"));

                        }

                        historyAdapter = new HistoryAdapter(c, operationName,operationDate,operationValue);
                        historyAdapter.notifyDataSetChanged();
                        history.setAdapter(historyAdapter);



                        progressBarCard.setVisibility(View.INVISIBLE);
                        numberCard.setVisibility(View.VISIBLE);
                        moneyCard.setVisibility(View.VISIBLE);
                        moneyTitleCard.setVisibility(View.VISIBLE);
                        bonusCard.setVisibility(View.VISIBLE);
                        bonusTitleCard.setVisibility(View.VISIBLE);
                        titleDetailCard.setVisibility(View.VISIBLE);
                        nameCard.setVisibility(View.VISIBLE);
                        history.setVisibility(View.VISIBLE);
                        payCard.setVisibility(View.VISIBLE);
                        imageDelete.setVisibility(View.VISIBLE);
                        imageEdit.setVisibility(View.VISIBLE);
                        updateCard.setVisibility(View.VISIBLE);
                        imageCard.setVisibility(View.VISIBLE);
                        closeDetail.setVisibility(View.VISIBLE);

                    } catch (IOException | JSONException e) {
                        Log.d(TAG, "Ошибка " + e);

                        progressBarCard.setVisibility(View.INVISIBLE);
                        numberCard.setVisibility(View.VISIBLE);
                        moneyCard.setVisibility(View.VISIBLE);
                        moneyTitleCard.setVisibility(View.VISIBLE);
                        bonusCard.setVisibility(View.VISIBLE);
                        bonusTitleCard.setVisibility(View.VISIBLE);
                        titleDetailCard.setVisibility(View.VISIBLE);
                        nameCard.setVisibility(View.VISIBLE);
                        history.setVisibility(View.VISIBLE);
                        payCard.setVisibility(View.VISIBLE);
                        imageDelete.setVisibility(View.VISIBLE);
                        imageEdit.setVisibility(View.VISIBLE);
                        updateCard.setVisibility(View.VISIBLE);
                        imageCard.setVisibility(View.VISIBLE);
                        closeDetail.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}

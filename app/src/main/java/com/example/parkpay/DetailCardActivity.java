package com.example.parkpay;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.StrictMode;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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

    private TextView numberCard;
    private TextView moneyCard;
    private TextView moneyTitleCard;
    private TextView bonusCard;
    private TextView bonusTitleCard;
    private TextView titleDetailCard;
    private ListView history;
    private TextView nameCard;
    private String cardNumber="";
    //Button deleteCard;
    private ImageView payCard;
    //Button editCard;
    private ImageView updateCard;
    private ImageView imageCard;
    private ImageView imageDelete;
    private ImageView imageEdit;
    private ImageView closeDetail;
    private Context c;
    private ProgressBar progressBarCard;

    private ArrayList<String> nameOperation;
    private ArrayList<String> dateOperation;
    private ArrayList<String> valueOperation;

    private ArrayList<String> operationName;
    private ArrayList<String> operationDate;
    private ArrayList<String> operationValue;

    private HistoryAdapter historyAdapter;

    private SharedPreferences settings;
    private ArrayList<String> child = new ArrayList<>();
    private ArrayList<String> children2 = new ArrayList<>();

    private static final String APP_PREFERENCES = "mysettings";
    private static final String APP_PREFERENCES_CARDS ="Cards";
    private static final String APP_PREFERENCES_TOKEN ="Token";
    private static final String APP_PREFERENCES_STATUS ="Status";
    private static final String APP_PREFERENCES_CARD_DELETE ="cardDelete";
    private static final String APP_PREFERENCES_CARD_NAME ="cardName";
    private static final String APP_PREFERENCES_CARD_CODE ="cardCode";
    private static final String APP_PREFERENCES_MONEY ="money";
    private static final String APP_PREFERENCES_BONUS ="bonus";
    private static final String APP_PREFERENCES_POSITION_CARD ="position";
    private static final String APP_PREFERENCES_POSITION_GROUP ="group";
    private static final String APP_PREFERENCES_NAMES_CARDS ="namesCards";
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
        numberCard= findViewById(R.id.numberCard);
        moneyCard= findViewById(R.id.moneyCard);
        moneyTitleCard= findViewById(R.id.moneyTitleCard);
        bonusCard= findViewById(R.id.bonusCard);
        bonusTitleCard= findViewById(R.id.bonusTitleCard);
        titleDetailCard= findViewById(R.id.titleDetailCard);
        nameCard= findViewById(R.id.nameCard);
        history= findViewById(R.id.historyList);
        payCard= findViewById(R.id.payCard);
        imageDelete= findViewById(R.id.imageDelete);
        imageEdit= findViewById(R.id.imageEdit);
        updateCard= findViewById(R.id.updateCard);
        imageCard= findViewById(R.id.imageCard);
        closeDetail= findViewById(R.id.closeDetail);
        progressBarCard= findViewById(R.id.progressBarCard);

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

        //int groupPosition=10;
        Toolbar toolbar = findViewById(R.id.toolbar);
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
        nameOperation= new ArrayList<>();
        dateOperation= new ArrayList<>();
        valueOperation = new ArrayList<>();

        operationName= new ArrayList<>();
        operationDate= new ArrayList<>();
        operationValue= new ArrayList<>();

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

        doGetRequest();

        closeDetail.setOnClickListener(v -> {

            Intent intent = new Intent(c,
                    MainActivity.class);
            startActivity(intent);

        });


        imageDelete.setOnClickListener(v -> {

            if(cardNumber!=null){

                AlertDialog.Builder builder = new AlertDialog.Builder(c);
                builder.setTitle("Удаление карты");
                builder.setMessage(Html
                        .fromHtml("<font color='#000000'>Вы действительно хотите удалить данную карту?</font>"));
                builder.setCancelable(false);
                builder.setPositiveButton("Да",
                        (dialog, id) -> {

                            Intent i = new Intent(c, MainActivity.class);

//                                    if(groupPosition==0){

                            if (settings.contains(APP_PREFERENCES_CARDS)) {
                                child = MainActivity.getArrayList(APP_PREFERENCES_CARDS, settings);
                                children2 = MainActivity.getArrayList(APP_PREFERENCES_NAMES_CARDS, settings);
                            }


                            doPostRequest("https://api.mobile.goldinnfish.com/card/delete");
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
                        });
                builder.setNegativeButton("Нет",
                        (dialog, id) -> dialog.cancel());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#3F51B5"));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackground(null);
            }
        });

        imageEdit.setOnClickListener(v -> {

            Intent intent = new Intent(c,
                    EditCardActivity.class);
            startActivity(intent);
        });

        payCard.setOnClickListener(v -> {
            Intent intent = new Intent(c,
                    PayActivity.class);
            startActivity(intent);
        });

        updateCard.setOnClickListener(v -> {

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

        });

    }

    private void doPostRequestCardInfo(String url){

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

                runOnUiThread(() -> {
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

                        getHistory("https://api.mobile.goldinnfish.com/card/history");

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

    private void doPostRequest(String url){

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

    private void doGetRequest(){

        OkHttpClient client = new OkHttpClient();

        HttpUrl mySearchUrl = new HttpUrl.Builder()
                .scheme("http")
                .host("api.mobile.goldinnfish.com")
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
            public void onResponse(@NotNull Call call, @NotNull final Response response) {
                runOnUiThread(() -> {
                    try {

                        String jsonData = null;
                        if (response.body() != null) {
                            jsonData = response.body().string();
                        }

                        JSONArray jsonArray = new JSONArray(jsonData);

                        child = new ArrayList<>();

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

    private void getHistory(String url) {

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
            public void onResponse(@NotNull Call call, @NotNull final Response response) {
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

    private static void setListViewHeightBasedOnChildren(ListView listView) {
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

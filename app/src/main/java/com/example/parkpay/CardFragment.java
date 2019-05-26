package com.example.parkpay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CardFragment extends Fragment {

    private static final String APP_PREFERENCES = "mysettings";
    private static final String APP_PREFERENCES_CARDS ="Cards";
    private static final String APP_PREFERENCES_NAMES_CARDS ="namesCards";
    public static final String APP_PREFERENCES_VIRTUAL_CARDS ="virtualCards";
    private static final String APP_PREFERENCES_TOKEN ="Token";
    private static final String APP_PREFERENCES_NAME ="Name";
    private static final String APP_PREFERENCES_NUMBER ="Number";
    private static final String APP_PREFERENCES_MAIL ="Email";
    private static final String APP_PREFERENCES_DATE_BIRTHDAY ="DateBirthday";
    private static final String APP_PREFERENCES_BONUS ="bonus";
    private static final String APP_PREFERENCES_QUANTITY_CARD ="quantityCard";
    public static final String APP_PREFERENCES_STATUS ="Status";
    private static final String APP_PREFERENCES_MONEY_CHILD ="moneyChild";
    private static final String APP_PREFERENCES_BONUS_CHILD ="bonusChild";
    private static final String APP_PREFERENCES_QUANTITY_VISITS ="quantityVisits";
    private static final String APP_PREFERENCES_PASSWORD ="Password";
    private static final String APP_PREFERENCES_LOGIN ="Login";
    private static final String APP_PREFERENCES_CARD_ID ="cardId";
    private static final String TAG = "myLogs";


    private List<Card> cards;
    private RecyclerView realCard;

    private CardAdapter cardAdapter;
    private ContentLoadingProgressBar progressBarCard;
    private SwipeRefreshLayout swipeCard;
    ImageView addCard;
    AppCompatTextView noCard;


    private SharedPreferences settings;
    private Context c;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_card,container,false);

        if (container != null) {
            c = container.getContext();
        }

        realCard = view.findViewById(R.id.realCard);
        addCard = view.findViewById(R.id.addCard);
        progressBarCard= view.findViewById(R.id.progressBarCard);
        swipeCard = view.findViewById(R.id.swipeCard);
        noCard = view.findViewById(R.id.noCard);

        swipeCard.setColorSchemeColors(Color.parseColor("#3F51B5"));


        settings= Objects.requireNonNull(this.getActivity())
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        realCard.setVisibility(View.INVISIBLE);
        progressBarCard.setVisibility(View.VISIBLE);
        noCard.setVisibility(View.INVISIBLE);

        LinearLayoutManager llm = new LinearLayoutManager(c);
        realCard.setLayoutManager(llm);

        swipeCard.setOnRefreshListener(() -> {


            boolean checkConnection=MainActivity.isOnline(c);

            if(checkConnection) {
                cardList();
            }
            else {

                swipeCard.setRefreshing(false);

                Toast.makeText(c, "Отсутствует интернет соединение!",
                        Toast.LENGTH_SHORT).show();
            }

        });

        StrictMode.ThreadPolicy mypolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(mypolicy);

        addCard.setOnClickListener(v -> {

            Intent intent = new Intent(c, AddCardActivity.class);
            c.startActivity(intent);
        });

        boolean checkConnection=MainActivity.isOnline(c);

        if(checkConnection) {

            refreshToken();

        }
        else {

            swipeCard.setRefreshing(false);

            noCard.setText("Отсутствует интернет соединение!");

            noCard.setVisibility(View.VISIBLE);

            realCard.setVisibility(View.VISIBLE);

            progressBarCard.setVisibility(View.INVISIBLE);

        }

        return view;
    }

    private void refreshToken(){

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        try {
            json.put("login",settings.getString(APP_PREFERENCES_LOGIN,""));
            json.put("password",settings.getString(APP_PREFERENCES_PASSWORD,""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonString = json.toString();
        RequestBody body = RequestBody.create(JSON, jsonString);
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .post(body)
                .url("https://api.mobile.goldinnfish.com/login")
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                if(call.request().body()!=null)
                {
                    Log.d(TAG, Objects.requireNonNull(call.request().body()).toString());
                }

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                    });
                }
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        try {

                            String jsonData = null;
                            if (response.body() != null) {
                                jsonData = response.body().string();
                            }

                            JSONObject Jobject = new JSONObject(jsonData);

                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString(APP_PREFERENCES_TOKEN, Jobject.getString("token"));
                            editor.apply();

                            cardList();

                            userInfo();

                            getVisits();

                        } catch (IOException | JSONException e) {

                            Log.d(TAG, "Ошибка: " + e);
                        }
                    });
                }
            }
        });
    }


    private void cardList(){

        OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder()
                .url("https://api.mobile.goldinnfish.com/card/list")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization","Bearer "+
                        Objects.requireNonNull(settings.getString(APP_PREFERENCES_TOKEN, "")))
                .get()
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                if(call.request().body()!=null)
                {
                    Log.d(TAG, Objects.requireNonNull(call.request().body()).toString());
                }

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                    });
                }
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) {

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        try {

                            String jsonData = null;
                            if (response.body() != null) {
                                jsonData = response.body().string();
                            }

                            JSONArray jsonArray = new JSONArray(jsonData);


                            cards = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject Jobject = jsonArray.getJSONObject(i);

                                cards.add(new Card(

                                        Jobject.getString("name"),
                                        Jobject.getString("code"),
                                        Jobject.getString("balance_money"),
                                        Jobject.getString("balance_bonus"),
                                        Jobject.getString("card_id")
                                ));

                            }
                            cardAdapter = new CardAdapter(c, cards);
                            realCard.setAdapter(cardAdapter);

                            realCard.setVisibility(View.VISIBLE);
                            progressBarCard.setVisibility(View.INVISIBLE);
                            noCard.setVisibility(View.INVISIBLE);

                            swipeCard.setRefreshing(false);


                        } catch (IOException | JSONException e) {

                            Log.d(TAG, "Ошибка " + e);

                            noCard.setVisibility(View.VISIBLE);

                            realCard.setVisibility(View.VISIBLE);

                            progressBarCard.setVisibility(View.INVISIBLE);

                            swipeCard.setRefreshing(false);
                        }
                    });
                }
            }
        });
    }

    private void userInfo(){

        OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder()
                .url("https://api.mobile.goldinnfish.com/user/get_info")
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("Authorization","Bearer "+
                        Objects.requireNonNull(settings.getString(APP_PREFERENCES_TOKEN, "")))
                .get()
                .build();
        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                if(call.request().body()!=null)
                {
                    Log.d(TAG, Objects.requireNonNull(call.request().body()).toString());
                }

                if (getActivity() != null) {

                    getActivity().runOnUiThread(() -> {
                    });
                }
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) {

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        try {

                            String jsonData = null;
                            if (response.body() != null) {
                                jsonData = response.body().string();
                            }

                            JSONObject parentObject = new JSONObject(jsonData);
                            JSONObject Jobject = parentObject.getJSONObject("user");

                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString(APP_PREFERENCES_NAME, Jobject.getString("name"));
                            editor.putString(APP_PREFERENCES_MAIL, Jobject.getString("email"));
                            editor.putString(APP_PREFERENCES_NUMBER, Jobject.getString("phone"));
                            editor.putString(APP_PREFERENCES_DATE_BIRTHDAY, Jobject.getString("birthday"));
                            editor.putString(APP_PREFERENCES_BONUS, Jobject.getString("bonus"));
                            editor.putString(APP_PREFERENCES_QUANTITY_CARD, Jobject.getString("quant_card"));
                            editor.apply();

                        } catch (IOException | JSONException e) {
                            Log.d(TAG, "Ошибка " + e);
                        }
                    });
                }
            }
        });
    }

    private void getVisits(){

        OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder()
                .url("https://api.mobile.goldinnfish.com/user/visits")
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("Authorization","Bearer "+
                        Objects.requireNonNull(settings.getString(APP_PREFERENCES_TOKEN, "")))
                .get()
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                if(call.request().body()!=null)
                {
                    Log.d(TAG, Objects.requireNonNull(call.request().body()).toString());
                }
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                    });
                }
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) {

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        try {

                            String jsonData = null;
                            if (response.body() != null) {
                                jsonData = response.body().string();
                            }

                            //JSONObject Jobject = new JSONObject(jsonData);

                            if (jsonData != null) {
                                if (!jsonData.contains("<"))
                                {
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putString(APP_PREFERENCES_QUANTITY_VISITS,jsonData);
                                    editor.apply();
                                }
                                else {
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putString(APP_PREFERENCES_QUANTITY_VISITS,"0");
                                    editor.apply();
                                }
                            }


                        } catch (IOException e) {
                            Log.d(TAG, "Ошибка " + e);
                        }
                    });
                }
            }
        });
    }

}

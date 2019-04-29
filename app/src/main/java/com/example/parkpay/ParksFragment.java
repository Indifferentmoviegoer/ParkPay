package com.example.parkpay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
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

public class ParksFragment extends Fragment {

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_TOKEN ="Token";
    public static final String APP_PREFERENCES_PARK_IDS ="parkIDs";
    public static final String APP_PREFERENCES_PARK_NAMES ="names";
    private static final String TAG = "myLogs";

    ArrayList<String> child;
    ArrayList<String> children2;
    ArrayList<String> moneyChild;
    ArrayList<String> bonusChild;
    ArrayList<String> idCard;

    ArrayList<String> children1;
    ArrayList<String> codes;
    ArrayList<String> money;
    ArrayList<String> bonus;
    ArrayList<String> cardId;

    ListView simpleList;
    ParksAdapter customAdapter;

    SharedPreferences settings;
    Context c;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_parks,container,false);

        if (container != null) {
            c = container.getContext();
        }

        simpleList = (ListView)view.findViewById(R.id.parks);


        settings= Objects.requireNonNull(this.getActivity())
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        //Создаем набор данных для адаптера
        child = new ArrayList<String>();
        children2 = new ArrayList<String>();
        moneyChild = new ArrayList<String>();
        bonusChild = new ArrayList<String>();
        idCard = new ArrayList<String>();

        children1 = new ArrayList<String>();
        codes = new ArrayList<String>();
        money = new ArrayList<String>();
        bonus = new ArrayList<String>();
        cardId = new ArrayList<String>();


        simpleList.invalidateViews();

        StrictMode.ThreadPolicy mypolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(mypolicy);

        boolean checkConnection=MainActivity.isOnline(c);

//        if(checkConnection) {

        doGetRequest();

//        }
//        else {
//            Toast.makeText(c, "Отсутствует интернет соединение!",
//                    Toast.LENGTH_SHORT).show();
//        }

        if(settings.contains(APP_PREFERENCES_PARK_IDS)){

            child=MainActivity.getArrayList(APP_PREFERENCES_PARK_IDS,settings);
        }

        if(settings.contains(APP_PREFERENCES_PARK_NAMES)){

            children2=MainActivity.getArrayList(APP_PREFERENCES_PARK_NAMES,settings);
        }

//        if(settings.contains(APP_PREFERENCES_MONEY_CHILD)){
//
//            moneyChild=MainActivity.getArrayList(APP_PREFERENCES_MONEY_CHILD,settings);
//        }
//
//        if(settings.contains(APP_PREFERENCES_BONUS_CHILD)){
//
//            bonusChild=MainActivity.getArrayList(APP_PREFERENCES_BONUS_CHILD,settings);
//        }
//
//        if(settings.contains(APP_PREFERENCES_CARD_ID)){
//
//            idCard=MainActivity.getArrayList(APP_PREFERENCES_CARD_ID,settings);
//        }

//        child.add("Новая карта1");
//        children2.add("Новая карта1");
//        moneyChild.add("Новая карта");
//        bonusChild.add("Новая карта");
//        idCard.add("Новая карта");
//
//        child.add("Новая карта2");
//        children2.add("Новая карта2");
//        moneyChild.add("Новая карта");
//        bonusChild.add("Новая карта");
//        idCard.add("Новая карта");
//
//        child.add("Новая карта3");
//        children2.add("Новая карта3");
//        moneyChild.add("Новая карта");
//        bonusChild.add("Новая карта");
//        idCard.add("Новая карта");

        //if(child) {
        customAdapter = new ParksAdapter(c,
                child,
                children2
//                moneyChild,
//                bonusChild,
//                idCard
        );
        simpleList.setAdapter(customAdapter);
        //}
        //else{
        //    simpleList.setVisibility(View.INVISIBLE);
        //}

        return view;
    }

    public void doGetRequest(){

        OkHttpClient client = new OkHttpClient();

        HttpUrl mySearchUrl = new HttpUrl.Builder()
                .scheme("http")
                .host("192.168.252.199")
                .addPathSegment("attr")
                .addPathSegment("parks")
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

                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                }
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        try {

                            String jsonData = null;
                            if (response.body() != null) {
                                jsonData = response.body().string();
                            }

                            JSONArray jsonArray = new JSONArray(jsonData);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject Jobject = jsonArray.getJSONObject(i);

                                Log.d(TAG, Jobject.getString("park_id"));
                                Log.d(TAG, Jobject.getString("name"));

                                children1.add(Jobject.getString("park_id"));
                                codes.add(Jobject.getString("name"));
//                                money.add(Jobject.getString("balance_money"));
//                                bonus.add(Jobject.getString("balance_bonus"));
//                                cardId.add(Jobject.getString("card_id"));

                            }

                            MainActivity.saveArrayList(children1, APP_PREFERENCES_PARK_IDS, settings);
                            MainActivity.saveArrayList(codes, APP_PREFERENCES_PARK_NAMES, settings);
//                            MainActivity.saveArrayList(money, APP_PREFERENCES_MONEY_CHILD, settings);
//                            MainActivity.saveArrayList(bonus, APP_PREFERENCES_BONUS_CHILD, settings);
//                            MainActivity.saveArrayList(cardId, APP_PREFERENCES_CARD_ID, settings);

                            children1.clear();
                            codes.clear();
//                            money.clear();
//                            bonus.clear();
//                            cardId.clear();



                            if(settings.contains(APP_PREFERENCES_PARK_IDS)){
                                children1=MainActivity.getArrayList(APP_PREFERENCES_PARK_IDS,settings);
                            }

                            if(settings.contains(APP_PREFERENCES_PARK_NAMES)){
                                codes=MainActivity.getArrayList(APP_PREFERENCES_PARK_NAMES,settings);
                            }

//                            if(settings.contains(APP_PREFERENCES_MONEY_CHILD)){
//                                money=MainActivity.getArrayList(APP_PREFERENCES_MONEY_CHILD,settings);
//                            }
//
//                            if(settings.contains(APP_PREFERENCES_BONUS_CHILD)){
//                                bonus=MainActivity.getArrayList(APP_PREFERENCES_BONUS_CHILD,settings);
//                            }
//
//                            if(settings.contains(APP_PREFERENCES_CARD_ID)){
//                                cardId=MainActivity.getArrayList(APP_PREFERENCES_CARD_ID,settings);
//                            }

//                            children1.add("Новая карта1");
//                            codes.add("Новая карта1");
//                            money.add("Новая карта");
//                            bonus.add("Новая карта");
//                            cardId.add("Новая карта");
//
//                            children1.add("Новая карта2");
//                            codes.add("Новая карта2");
//                            money.add("Новая карта");
//                            bonus.add("Новая карта");
//                            cardId.add("Новая карта");
//
//                            children1.add("Новая карта3");
//                            codes.add("Новая карта3");
//                            money.add("Новая карта");
//                            bonus.add("Новая карта");
//                            cardId.add("Новая карта");


                            customAdapter = new ParksAdapter(c,
                                    children1,
                                    codes
//                                    money,
//                                    bonus,
//                                    cardId
                            );
                            simpleList.setAdapter(customAdapter);


                        } catch (IOException | JSONException e) {

                            Log.d(TAG, "Ошибка " + e);
                        }
                    });
                }
            }
        });
    }

}

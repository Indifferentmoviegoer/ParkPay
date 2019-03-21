package com.example.parkpay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CardFragment extends Fragment {

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_CARDS ="Cards";
    public static final String APP_PREFERENCES_VIRTUAL_CARDS ="virtualCards";
    public static final String APP_PREFERENCES_TOKEN ="Token";
    private static final String TAG = "myLogs";
    SharedPreferences settings;
    Context c;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_card,container,false);
        if (container != null) {
            c = container.getContext();
        }
        // Находим наш list
        ExpandableListView listView = (ExpandableListView)view.findViewById(R.id.exListView);
        settings= Objects.requireNonNull(this.getActivity())
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        //Создаем набор данных для адаптера
        ArrayList<ArrayList<String>> groups = new ArrayList<ArrayList<String>>();
        ArrayList<String> children1 = new ArrayList<String>();
        ArrayList<String> children2 = new ArrayList<String>();

        if(settings.contains(APP_PREFERENCES_CARDS)){
            children1=MainActivity.getArrayList(APP_PREFERENCES_CARDS,settings);
        }

        children1.add("Новая карта");
        groups.add(children1);

        if(settings.contains(APP_PREFERENCES_VIRTUAL_CARDS)){
            children2=MainActivity.getArrayList(APP_PREFERENCES_VIRTUAL_CARDS,settings);
        }

        children2.add("Новая карта");
        groups.add(children2);
        //Создаем адаптер и передаем context и список с данными
        ExpListAdapter adapter = new ExpListAdapter(this.getActivity(), groups);
        listView.setAdapter(adapter);
        return view;
    }

    public void doGetRequest(String url){

        OkHttpClient client = new OkHttpClient();

        HttpUrl mySearchUrl = new HttpUrl.Builder()
                .scheme("http")
                .host("192.168.252.199")
                .addPathSegment("user")
                .addPathSegment("get_info")
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
                Log.v(TAG, Objects.requireNonNull(call.request().body()).toString());
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                    try {

                        String jsonData = null;
                        if (response.body() != null) {
                            jsonData = response.body().string();
                        }

                        JSONObject parentObject = new JSONObject(jsonData);
                        JSONObject Jobject = parentObject.getJSONObject("user");

                        Log.d(TAG,Jobject.getString("card_id"));
                        Log.d(TAG,Jobject.getString("name"));
                        Log.d(TAG,Jobject.getString("code"));

                        ArrayList<String> children1 = new ArrayList<String>();

                        SharedPreferences.Editor editor = settings.edit();

//                        child.add(numberCard);

                        children1=MainActivity.getArrayList(APP_PREFERENCES_CARDS,settings);

                        MainActivity.saveArrayList(children1, APP_PREFERENCES_CARDS,settings);

//                        editor.putString(APP_PREFERENCES_NAME,Jobject.getString("card_id"));
//                        editor.putString(APP_PREFERENCES_MAIL,Jobject.getString("name"));
//                        editor.putString(APP_PREFERENCES_NUMBER,Jobject.getString("code"));
                        editor.apply();

                    } catch (IOException | JSONException e) {
                        Log.d(TAG,"Ошибка "+e);
                    }
                });
            }
        });
    }

}

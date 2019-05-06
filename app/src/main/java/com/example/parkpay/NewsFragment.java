package com.example.parkpay;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

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
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsFragment extends Fragment {

    RecyclerView rv;
    ProgressBar progressBarNews;
    Context c;

    String[] news = {"Новости", "Акции"};

    private List<News> persons;
    private List<Sale> sales;

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_CARD ="Card";
    public static final String APP_PREFERENCES_TOKEN ="Token";
    SharedPreferences settings;

    private static final String TAG = "myLogs";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_news,container,false);

        progressBarNews=(ProgressBar) view.findViewById(R.id.progressBarNews);
        rv = (RecyclerView)view.findViewById(R.id.rv);

        settings= Objects.requireNonNull(this.getActivity())
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        rv.setVisibility(View.INVISIBLE);

        c=getContext();

        Spinner spinner = (Spinner) view.findViewById(R.id.newsTitle);
        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        ArrayAdapter<String> adapterNews = new ArrayAdapter<String>(c, R.layout.spinner_item, news);
        // Определяем разметку для использования при выборе элемента
        adapterNews.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        spinner.setAdapter(adapterNews);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){

                    getNews();
                }
                if(position==1){

                    getSales();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                spinner.setSelection(0);
            }
        });


        LinearLayoutManager llm = new LinearLayoutManager(c);
        rv.setLayoutManager(llm);

        //initializeData();
        getNews();


        return view;
    }

    public void getNews(){

        OkHttpClient client = new OkHttpClient();

        HttpUrl mySearchUrl = new HttpUrl.Builder()
                .scheme("http")
                .host("192.168.252.199")
                .addPathSegment("news")
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
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
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

                            persons = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject Jobject = jsonArray.getJSONObject(i);

                                Log.d(TAG, Jobject.getString("title"));
                                Log.d(TAG, Jobject.getString("image"));
                                Log.d(TAG, Jobject.getString("date"));
                                Log.d(TAG, Jobject.getString("text"));
                                Log.d(TAG, Jobject.getString("link_sourc"));

                                persons.add(new News(
                                        Jobject.getString("title"),
                                        Jobject.getString("image"),
                                        Jobject.getString("date"),
                                        Jobject.getString("text"),
                                        Jobject.getString("link_source")
                                ));

                            }

                            NewsAdapter adapter = new NewsAdapter(c,persons);
                            rv.setAdapter(adapter);

                            rv.setVisibility(View.VISIBLE);
                            progressBarNews.setVisibility(View.INVISIBLE);

                        } catch (IOException| JSONException e) {
                            Log.d(TAG, "Ошибка " + e);
                        }
                    });
                }
            }
        });
    }

    public void getSales(){

        OkHttpClient client = new OkHttpClient();

        HttpUrl mySearchUrl = new HttpUrl.Builder()
                .scheme("http")
                .host("192.168.252.199")
                .addPathSegment("stocks")
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
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
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

                            sales = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject Jobject = jsonArray.getJSONObject(i);

                                Log.d(TAG, Jobject.getString("title"));
                                Log.d(TAG, Jobject.getString("text"));
                                Log.d(TAG, Jobject.getString("date_start"));
                                Log.d(TAG, Jobject.getString("date_end"));
                                Log.d(TAG, Jobject.getString("image"));

                                sales.add(new Sale(
                                        Jobject.getString("title"),
                                        Jobject.getString("text"),
                                        Jobject.getString("date_start"),
                                        Jobject.getString("date_end"),
                                        Jobject.getString("image")
                                ));

                            }

                            SalesAdapter adapter = new SalesAdapter(c,sales);
                            rv.setAdapter(adapter);

                            rv.setVisibility(View.VISIBLE);
                            progressBarNews.setVisibility(View.INVISIBLE);


                        } catch (IOException|JSONException e) {
                            Log.d(TAG, "Ошибка " + e);
                        }
                    });
                }
            }
        });
    }

}
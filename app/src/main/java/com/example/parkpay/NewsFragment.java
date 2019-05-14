package com.example.parkpay;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsFragment extends Fragment {

    private RecyclerView rv;
    private ProgressBar progressBarNews;
    private SwipeRefreshLayout swipeNews;
    private Context c;

    private final String[] news = {"Новости", "Акции"};

    private List<News> persons;
    private List<Sale> sales;

    private static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_CARD ="Card";
    private static final String APP_PREFERENCES_TOKEN ="Token";
    private SharedPreferences settings;

    private static final String TAG = "myLogs";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_news,container,false);

        progressBarNews= view.findViewById(R.id.progressBarNews);
        rv = view.findViewById(R.id.rv);
        swipeNews = view.findViewById(R.id.swipeNews);

        swipeNews.setColorSchemeColors(Color.parseColor("#3F51B5"));

        settings= Objects.requireNonNull(this.getActivity())
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        rv.setVisibility(View.INVISIBLE);

        c=getContext();

        Spinner spinner = view.findViewById(R.id.newsTitle);
        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        ArrayAdapter<String> adapterNews = new ArrayAdapter<>(c, R.layout.spinner_item, news);
        // Определяем разметку для использования при выборе элемента
        adapterNews.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        spinner.setAdapter(adapterNews);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){

//                    boolean checkConnection=MainActivity.isOnline(c);
//
//                    if(checkConnection){
                    getNews();
//                    }
//                    else {
//                        Toast.makeText(c, "Отсутствует интернет соединение!",
//                                Toast.LENGTH_SHORT).show();
//                    }

                    swipeNews.setOnRefreshListener(() -> new Handler().postDelayed(() -> {

                        swipeNews.setRefreshing(false);


//                        if(checkConnection){

                        getNews();

//                        }
//                        else {
//                            Toast.makeText(c, "Отсутствует интернет соединение!",
//                                    Toast.LENGTH_SHORT).show();
//                        }

                    }, 5000));
                }
                if(position==1){

                    getSales();

                    swipeNews.setOnRefreshListener(() -> new Handler().postDelayed(() -> {

                        swipeNews.setRefreshing(false);

                        getSales();

                    }, 5000));
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

//        boolean checkConnection=MainActivity.isOnline(c);
//
//        if(checkConnection){
            getNews();
//        }
//        else {
//            Toast.makeText(c, "Отсутствует интернет соединение!",
//                    Toast.LENGTH_SHORT).show();
//        }

        return view;
    }

    private void getNews(){

        OkHttpClient client = new OkHttpClient();

        HttpUrl mySearchUrl = new HttpUrl.Builder()
                .scheme("https")
                .host("api.mobile.goldinnfish.com")
                .addPathSegment("news")
                .addPathSegment("list")
                .build();

        Log.d(TAG,mySearchUrl.toString());

        final Request request = new Request.Builder()
                .url(mySearchUrl)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization","Bearer "+
                        Objects.requireNonNull(settings.getString(APP_PREFERENCES_TOKEN, ""))
                )
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
            public void onResponse(@NotNull Call call, @NotNull final Response response) {

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
                                Log.d(TAG, Jobject.getString("link_source"));

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

    private void getSales(){

        OkHttpClient client = new OkHttpClient();

        HttpUrl mySearchUrl = new HttpUrl.Builder()
                .scheme("https")
                .host("api.mobile.goldinnfish.com")
                .addPathSegment("stocks")
                .addPathSegment("list")
                .build();

        Log.d(TAG,mySearchUrl.toString());
        Log.d(TAG,Objects.requireNonNull(settings.getString(APP_PREFERENCES_TOKEN, "")));

        final Request request = new Request.Builder()
                .url(mySearchUrl)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
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
            public void onResponse(@NotNull Call call, @NotNull final Response response) {

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
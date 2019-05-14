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
import android.widget.ProgressBar;

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

public class ParksFragment extends Fragment {

    private static final String APP_PREFERENCES = "mysettings";
    private static final String APP_PREFERENCES_TOKEN ="Token";
    public static final String APP_PREFERENCES_PARK_IDS ="parkIDs";
    public static final String APP_PREFERENCES_PARK_NAMES ="names";
    public static final String APP_PREFERENCES_PARK_ID ="parkID";
    private static final String TAG = "myLogs";

    private RecyclerView rv;
    private ProgressBar progressBarParks;
    private SwipeRefreshLayout sRL;

    private List<Park> parks;

    private SharedPreferences settings;
    private Context c;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_parks,container,false);

        progressBarParks= view.findViewById(R.id.progressBarParks);
        rv = view.findViewById(R.id.rvParks);
        sRL = view.findViewById(R.id.sRL);

        sRL.setColorSchemeColors(Color.parseColor("#3F51B5"));

        settings= Objects.requireNonNull(this.getActivity())
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        rv.setVisibility(View.INVISIBLE);

        c=getContext();

        LinearLayoutManager llm = new LinearLayoutManager(c);
        rv.setLayoutManager(llm);



        sRL.setOnRefreshListener(() -> {

            new Handler().postDelayed(() -> {

                sRL.setRefreshing(false);

                parks = new ArrayList<>();

                rv.setVisibility(View.INVISIBLE);

                getParks();

                ParksAdapter adapter = new ParksAdapter(c,parks);
                rv.setAdapter(adapter);

                rv.setVisibility(View.VISIBLE);
                progressBarParks.setVisibility(View.INVISIBLE);
            }, 5000);
        });

//        ParksAdapter adapter = new ParksAdapter(c,parks);
//        rv.setAdapter(adapter);
//
//        rv.setVisibility(View.VISIBLE);
//        progressBarParks.setVisibility(View.INVISIBLE);

        getParks();



        return view;
    }


    public void getParks(){

        OkHttpClient client = new OkHttpClient();

        HttpUrl mySearchUrl = new HttpUrl.Builder()
                .scheme("https")
                .host("api.mobile.goldinnfish.com")
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

                            parks = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject Jobject = jsonArray.getJSONObject(i);

                                Log.d(TAG, Jobject.getString("park_id"));
                                Log.d(TAG, Jobject.getString("name"));
                                Log.d(TAG, Jobject.getString("lat_top"));
                                Log.d(TAG, Jobject.getString("lng_top"));
                                Log.d(TAG, Jobject.getString("lat_bottom"));
                                Log.d(TAG, Jobject.getString("lng_bottom"));
                                Log.d(TAG, Jobject.getString("lat_center"));
                                Log.d(TAG, Jobject.getString("lng_center"));

                                String lat=Jobject.getString("lat_center");
                                String lng=Jobject.getString("lng_center");

                                if(lat.contains("null")){
                                    lat="0.0";
                                }
                                if(lng.contains("null")){
                                    lng="0.0";
                                }
                                parks.add(new Park(
                                        Jobject.getString("park_id"),
                                        Jobject.getString("name"),
//                                        Float.parseFloat(Jobject.getString("lat_top")),
//                                        Float.parseFloat(Jobject.getString("lng_top")),
//                                        Float.parseFloat(Jobject.getString("lat_bottom")),
//                                        Float.parseFloat(Jobject.getString("lng_bottom")),
                                        Float.parseFloat(lat),
                                        Float.parseFloat(lng),
                                        "https://cms-assets.tutsplus.com/uploads/users/1499/posts/28207/image/ty.JPG"
                                ));

                            }


                            ParksAdapter adapter = new ParksAdapter(c,parks);
                            rv.setAdapter(adapter);

                            rv.setVisibility(View.VISIBLE);
                            progressBarParks.setVisibility(View.INVISIBLE);

                        } catch (IOException | JSONException e) {

                            Log.d(TAG, "Ошибка " + e);
                        }
                    });
                }
            }
        });
    }

}

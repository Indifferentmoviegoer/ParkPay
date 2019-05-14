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
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptor;

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

public class AttractionsFragment extends Fragment {

    private static final String APP_PREFERENCES = "mysettings";
    private static final String APP_PREFERENCES_TOKEN ="Token";
    private static final String APP_PREFERENCES_PARK_ID ="parkID";
    private static final String TAG = "myLogs";

    private RecyclerView rv;
    private ProgressBar progressBarAttractions;
    private SwipeRefreshLayout swipeAttr;

    private List<Attraction> attr;

    private SharedPreferences settings;
    private Context c;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_attractions,container,false);

        if (container != null) {
            c = container.getContext();
        }

        //simpleList = (ListView)view.findViewById(R.id.parks);

        progressBarAttractions= view.findViewById(R.id.progressBarAttractions);
        rv = view.findViewById(R.id.attrs);
        swipeAttr = view.findViewById(R.id.swipeAttr);

        swipeAttr.setColorSchemeColors(Color.parseColor("#3F51B5"));

        settings= Objects.requireNonNull(this.getActivity())
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        rv.setVisibility(View.INVISIBLE);

        c=getContext();


        LinearLayoutManager llm = new LinearLayoutManager(c);
        rv.setLayoutManager(llm);



        swipeAttr.setOnRefreshListener(() -> {

            new Handler().postDelayed(() -> {

                swipeAttr.setRefreshing(false);

                attr = new ArrayList<>();

                rv.setVisibility(View.INVISIBLE);

                getAttr("https://api.mobile.goldinnfish.com/attr/list");

                AttractionsAdapter adapter = new AttractionsAdapter(c,attr);
                rv.setAdapter(adapter);

                rv.setVisibility(View.VISIBLE);
                progressBarAttractions.setVisibility(View.INVISIBLE);
            }, 5000);
        });

//        AttractionsAdapter adapter = new AttractionsAdapter(c,attr);
//        rv.setAdapter(adapter);
//
//        rv.setVisibility(View.VISIBLE);
//        progressBarAttractions.setVisibility(View.INVISIBLE);

        getAttr("https://api.mobile.goldinnfish.com/attr/list");


        return view;
    }

    public void getAttr(String url){

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        try {
            json.put("park_id",settings.getString(APP_PREFERENCES_PARK_ID, ""));

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

                            BitmapDescriptor markerIcon;

                            String jsonData = null;
                            if (response.body() != null) {
                                jsonData = response.body().string();
                            }

                            JSONArray jsonArray = new JSONArray(jsonData);

                            attr = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject Jobject = jsonArray.getJSONObject(i);

                                Log.d(TAG, Jobject.getString("atr_id"));
                                Log.d(TAG, Jobject.getString("rep_name"));
                                Log.d(TAG, Jobject.getString("image"));
                                Log.d(TAG, Jobject.getString("price"));
                                Log.d(TAG, Jobject.getString("bonus"));
                                Log.d(TAG, Jobject.getString("text"));
                                Log.d(TAG, Jobject.getString("weight"));
                                Log.d(TAG, Jobject.getString("growth"));
                                Log.d(TAG, Jobject.getString("age_min"));
                                Log.d(TAG, Jobject.getString("age_max"));
                                Log.d(TAG, Jobject.getString("level_fear"));
                                Log.d(TAG, Jobject.getString("lat"));
                                Log.d(TAG, Jobject.getString("lng"));

                                String lat=Jobject.getString("lat");
                                String lng=Jobject.getString("lng");

                                if(lat.contains("null")){
                                    lat="0.0";
                                }
                                if(lng.contains("null")){
                                    lng="0.0";
                                }

                                attr.add(new Attraction(
                                        Jobject.getString("atr_id"),
                                        Jobject.getString("rep_name"),
                                        Jobject.getString("image"),
                                        Jobject.getString("price"),
                                        Jobject.getString("bonus"),
                                        Jobject.getString("text"),
                                        Jobject.getString("weight"),
                                        Jobject.getString("growth"),
                                        Jobject.getString("age_min"),
                                        Jobject.getString("age_max"),
                                        Jobject.getString("level_fear"),
                                        Float.parseFloat(lat),
                                        Float.parseFloat(lng)
                                ));

                            }

                            AttractionsAdapter adapter = new AttractionsAdapter(c,attr);
                            rv.setAdapter(adapter);

                            rv.setVisibility(View.VISIBLE);
                            progressBarAttractions.setVisibility(View.INVISIBLE);

                        } catch (IOException | JSONException e) {
                            Toast.makeText(c, "Ошибка " + e, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

}

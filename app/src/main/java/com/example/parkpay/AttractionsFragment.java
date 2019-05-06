package com.example.parkpay;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AttractionsFragment extends Fragment {

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_TOKEN ="Token";
    public static final String APP_PREFERENCES_PARK_ID ="parkID";
    private static final String TAG = "myLogs";

    RecyclerView rv;
    ProgressBar progressBarAttractions;

    private List<Attraction> attr;

    SharedPreferences settings;
    Context c;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_attractions,container,false);

        if (container != null) {
            c = container.getContext();
        }

        //simpleList = (ListView)view.findViewById(R.id.parks);


        settings= Objects.requireNonNull(this.getActivity())
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        progressBarAttractions=(ProgressBar) view.findViewById(R.id.progressBarAttractions);
        rv = (RecyclerView)view.findViewById(R.id.rvParks);

        settings= Objects.requireNonNull(this.getActivity())
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        rv.setVisibility(View.INVISIBLE);

        c=getContext();


        LinearLayoutManager llm = new LinearLayoutManager(c);
        rv.setLayoutManager(llm);

        attr = new ArrayList<>();

        attr.add(new Attraction(
                "1",
                "1",
                "https://farikqwerty.000webhostapp.com/pic/Samira.jpg",
                "s",
                "s",
                "s",
                "s",
                "s",
                "s",
                "s",
                "s",
                0.1f,
                0.1f
        ));

        attr.add(new Attraction(
                "2",
                "2",
                "https://farikqwerty.000webhostapp.com/pic/sima.jpg",
                "s",
                "s",
                "s",
                "s",
                "s",
                "s",
                "s",
                "s",
                0.1f,
                0.1f
        ));

        attr.add(new Attraction(
                "3",
                "3",
                "https://farikqwerty.000webhostapp.com/pic/sima.jpg",
                "s",
                "s",
                "s",
                "s",
                "s",
                "s",
                "s",
                "s",
                0.1f,
                0.1f
        ));

        attr.add(new Attraction(
                "4",
                "4",
                "https://cms-assets.tutsplus.com/uploads/users/1499/posts/28207/image/ty.JPG",
                "s",
                "s",
                "s",
                "s",
                "s",
                "s",
                "s",
                "s",
                0.1f,
                0.1f
        ));


        attr.add(new Attraction(
                "5",
                "5",
                "https://cms-assets.tutsplus.com/uploads/users/1499/posts/28207/image/ty.JPG",
                "s",
                "s",
                "s",
                "s",
                "s",
                "s",
                "s",
                "s",
                0.1f,
                0.1f
        ));

        AttractionsAdapter adapter = new AttractionsAdapter(c,attr);
        rv.setAdapter(adapter);

        rv.setVisibility(View.VISIBLE);
        progressBarAttractions.setVisibility(View.INVISIBLE);

        //getAttr("http://192.168.252.199/attr/list");


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

                            BitmapDescriptor markerIcon;

                            String jsonData = null;
                            if (response.body() != null) {
                                jsonData = response.body().string();
                            }

                            JSONArray jsonArray = new JSONArray(jsonData);

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
                                        Float.parseFloat(Jobject.getString("lat")),
                                        Float.parseFloat(Jobject.getString("lng"))
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

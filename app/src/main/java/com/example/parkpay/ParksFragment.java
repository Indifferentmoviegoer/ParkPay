package com.example.parkpay;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
    AppCompatTextView noResult;

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
        noResult = view.findViewById(R.id.noResult);

        sRL.setColorSchemeColors(Color.parseColor("#3F51B5"));

        settings= Objects.requireNonNull(this.getActivity())
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        rv.setVisibility(View.INVISIBLE);

        c=getContext();

        LinearLayoutManager llm = new LinearLayoutManager(c);
        rv.setLayoutManager(llm);

        progressBarParks.setVisibility(View.VISIBLE);
        noResult.setVisibility(View.INVISIBLE);


        sRL.setOnRefreshListener(() -> {



//                progressBarParks.setVisibility(View.VISIBLE);
//                rv.setVisibility(View.INVISIBLE);

                parks = new ArrayList<>();

            boolean checkConnection=MainActivity.isOnline(c);

            if(checkConnection) {

                getParks();
            }
            else {

                rv.setVisibility(View.VISIBLE);
                progressBarParks.setVisibility(View.INVISIBLE);

                sRL.setRefreshing(false);


                Toast.makeText(c, "Отсутствует интернет соединение!",
                        Toast.LENGTH_SHORT).show();

            }
        });

        boolean checkConnection=MainActivity.isOnline(c);

        if(checkConnection) {

        getParks();

        }
        else {

            rv.setVisibility(View.VISIBLE);
            progressBarParks.setVisibility(View.INVISIBLE);

            sRL.setRefreshing(false);

            noResult.setText("Отсутствует интернет соединение!");

            noResult.setVisibility(View.VISIBLE);

        }

        return view;
    }


    private void getParks(){

        OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder()
                .url("https://api.mobile.goldinnfish.com/attr/parks")
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

                            JSONArray jsonArray = new JSONArray(jsonData);

                            parks = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject Jobject = jsonArray.getJSONObject(i);

                                String lat=Jobject.getString("lat_center");
                                String lng=Jobject.getString("lng_center");
                                String photo="https://wikiway.com/upload/hl-photo/9dc/391/park_solnechniy_ostrov_53.jpg";

                                if(lat.contains("null")){
                                    lat="0.0";
                                }
                                if(lng.contains("null")){
                                    lng="0.0";
                                }

                                if(Jobject.getString("park_id").contains("1")){

                                    photo="http://samaratoday.ru/img/2017/02/pob-pre-wm-3.jpg";
                                }
                                else if(Jobject.getString("park_id").contains("3")){

                                    photo="http://4.bp.blogspot.com/-wmOXI9G_Guo/ToS3MFecxXI/AAAAAAAABDY/Gm701iyVs9E/s1600/DSC_0351.jpg";
                                }
                                else if(Jobject.getString("park_id").contains("6")){

                                    photo="https://upload.wikimedia.org/wikipedia/commons/thumb/6/66/%D0%9F%D0%B5%D1%80%D0%B2%D0%BE%D0%BC%D0%B0%D0%B9%D1%81%D0%BA%D0%B0%D1%8F_%D1%80%D0%BE%D1%89%D0%B0_%28%D0%A7%D0%B8%D1%81%D1%82%D1%8F%D0%BA%D0%BE%D0%B2%D1%81%D0%BA%D0%B0%D1%8F_%D1%80%D0%BE%D1%89%D0%B0%29%2C_%D0%9A%D1%80%D0%B0%D1%81%D0%BD%D0%BE%D0%B4%D0%B0%D1%80_01.JPG/1200px-%D0%9F%D0%B5%D1%80%D0%B2%D0%BE%D0%BC%D0%B0%D0%B9%D1%81%D0%BA%D0%B0%D1%8F_%D1%80%D0%BE%D1%89%D0%B0_%28%D0%A7%D0%B8%D1%81%D1%82%D1%8F%D0%BA%D0%BE%D0%B2%D1%81%D0%BA%D0%B0%D1%8F_%D1%80%D0%BE%D1%89%D0%B0%29%2C_%D0%9A%D1%80%D0%B0%D1%81%D0%BD%D0%BE%D0%B4%D0%B0%D1%80_01.JPG";
                                }
                                else if(Jobject.getString("park_id").contains("7")){

                                    photo="https://wikiway.com/upload/hl-photo/9dc/391/park_solnechniy_ostrov_53.jpg";
                                }
                                else{

                                    photo="https://wikiway.com/upload/hl-photo/9dc/391/park_solnechniy_ostrov_53.jpg";
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
                                        photo
                                ));
                            }


                            ParksAdapter adapter = new ParksAdapter(c,parks);
                            rv.setAdapter(adapter);

                            rv.setVisibility(View.VISIBLE);
                            progressBarParks.setVisibility(View.INVISIBLE);

                            sRL.setRefreshing(false);

                        } catch (IOException | JSONException e) {

                            rv.setVisibility(View.VISIBLE);
                            progressBarParks.setVisibility(View.INVISIBLE);

                            sRL.setRefreshing(false);

                            noResult.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
    }

}

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Spinner;
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

public class ParksFragment extends Fragment {

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_TOKEN ="Token";
    public static final String APP_PREFERENCES_PARK_IDS ="parkIDs";
    public static final String APP_PREFERENCES_PARK_NAMES ="names";
    public static final String APP_PREFERENCES_PARK_ID ="parkID";
    private static final String TAG = "myLogs";

    RecyclerView rv;
    ProgressBar progressBarParks;

    private List<Park> parks;

    SharedPreferences settings;
    Context c;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_parks,container,false);

        progressBarParks=(ProgressBar) view.findViewById(R.id.progressBarParks);
        rv = (RecyclerView)view.findViewById(R.id.rvParks);

        settings= Objects.requireNonNull(this.getActivity())
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        rv.setVisibility(View.INVISIBLE);

        c=getContext();


        LinearLayoutManager llm = new LinearLayoutManager(c);
        rv.setLayoutManager(llm);

        parks = new ArrayList<>();

        parks.add(new Park(
                "1",
                "1",
                0.1f,
                0.1f,
                0.1f,
                0.1f,
                0.1f,
                0.1f,
                "https://farikqwerty.000webhostapp.com/pic/Samira.jpg"
        ));

        parks.add(new Park(
                "2",
                "2",
                0.1f,
                0.1f,
                0.1f,
                0.1f,
                0.1f,
                0.1f,
                "https://farikqwerty.000webhostapp.com/pic/sima.jpg"
        ));

        parks.add(new Park(
                "3",
                "3",
                0.1f,
                0.1f,
                0.1f,
                0.1f,
                0.1f,
                0.1f,
                "https://farikqwerty.000webhostapp.com/pic/sima.jpg"
        ));

        parks.add(new Park(
                "4",
                "4",
                0.1f,
                0.1f,
                0.1f,
                0.1f,
                0.1f,
                0.1f,
                "https://cms-assets.tutsplus.com/uploads/users/1499/posts/28207/image/ty.JPG"
        ));


        parks.add(new Park(
                "5",
                "5",
                0.1f,
                0.1f,
                0.1f,
                0.1f,
                0.1f,
                0.1f,
                "https://cms-assets.tutsplus.com/uploads/users/1499/posts/28207/image/ty.JPG"
        ));

        ParksAdapter adapter = new ParksAdapter(c,parks);
        rv.setAdapter(adapter);

        rv.setVisibility(View.VISIBLE);
        progressBarParks.setVisibility(View.INVISIBLE);

        //getParks();



        return view;
    }


    public void getParks(){

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
                                Log.d(TAG, Jobject.getString("lat_top"));
                                Log.d(TAG, Jobject.getString("lng_top"));
                                Log.d(TAG, Jobject.getString("lat_bottom"));
                                Log.d(TAG, Jobject.getString("lng_bottom"));
                                Log.d(TAG, Jobject.getString("lat_center"));
                                Log.d(TAG, Jobject.getString("lng_center"));

                                parks.add(new Park(
                                        Jobject.getString("park_id"),
                                        Jobject.getString("name"),
                                        Float.parseFloat(Jobject.getString("lat_top")),
                                        Float.parseFloat(Jobject.getString("lng_top")),
                                        Float.parseFloat(Jobject.getString("lat_bottom")),
                                        Float.parseFloat(Jobject.getString("lng_bottom")),
                                        Float.parseFloat(Jobject.getString("lat_center")),
                                        Float.parseFloat(Jobject.getString("lng_center")),
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

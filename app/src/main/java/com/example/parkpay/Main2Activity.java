package com.example.parkpay;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Main2Activity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap gmap;
    private Context c;

    private static final String MAP_VIEW_BUNDLE_KEY = "AIzaSyAkEPoaujC2ZE7bNYANSH4J7XV-UgzVfSY";

    private static final String APP_PREFERENCES = "mysettings";
    private static final String APP_PREFERENCES_TOKEN ="Token";
    private static final String APP_PREFERENCES_PARK_ID ="parkID";
    private static final String APP_PREFERENCES_LAT ="lat";
    private static final String APP_PREFERENCES_LNG ="lng";
    private static final String APP_PREFERENCES_NAME_OBJECT ="nameObject";
    private SharedPreferences settings;

    private static final String TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        c=this;

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        settings= Objects.requireNonNull(c)
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

       // getAttr("http://192.168.252.199/attr/list");

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMinZoomPreference(16);

        gmap.setIndoorEnabled(true);
        UiSettings uiSettings = gmap.getUiSettings();
        uiSettings.setIndoorLevelPickerEnabled(true);
        uiSettings.setMyLocationButtonEnabled(false);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        //uiSettings.setScrollGesturesEnabled(false);
        //uiSettings.setTiltGesturesEnabled(false);

        LatLng ny = new LatLng(
                settings.getFloat(APP_PREFERENCES_LAT,0),
                settings.getFloat(APP_PREFERENCES_LNG,0)
        );

        addMarker();

        gmap.moveCamera(CameraUpdateFactory.newLatLng(ny));
    }

    private void addMarker(){

        BitmapDescriptor markerIcon;

        if(null != gmap){

            Drawable circleDrawable = ContextCompat.getDrawable(c, R.drawable.ic_carousel);
            markerIcon = getMarkerIconFromDrawable(Objects.requireNonNull(circleDrawable));

            gmap.addMarker(new MarkerOptions()
                    .position(new LatLng(
                            settings.getFloat(APP_PREFERENCES_LAT,0),
                            settings.getFloat(APP_PREFERENCES_LNG,0)
                    ))
                    .title(settings.getString(APP_PREFERENCES_NAME_OBJECT,""))
                    //.snippet("Аттракцион 1")
                    .icon(markerIcon)
            );
//
//            gmap.addMarker(new MarkerOptions()
//                    .position(new LatLng(45.058189, 38.991636))
//                    .title("Аттракцион 2")
//                    .snippet("Аттракцион 2")
//                    .icon(markerIcon)
//            );
//
//            gmap.addMarker(new MarkerOptions()
//                    .position(new LatLng(45.057458, 38.991373))
//                    .title("Аттракцион 3")
//                    .snippet("Аттракцион 3")
//                    .icon(markerIcon)
//            );
        }
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
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

                runOnUiThread(() -> {
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) {
                runOnUiThread(() -> {
                    try {

                        BitmapDescriptor markerIcon;

                        String jsonData = null;
                        if (response.body() != null) {
                            jsonData = response.body().string();
                        }

                        JSONArray jsonArray = new JSONArray(jsonData);

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject Jobject = jsonArray.getJSONObject(i);

                            Log.d(TAG, Jobject.getString("card_id"));
                            Log.d(TAG, Jobject.getString("name"));
                            Log.d(TAG, Jobject.getString("code"));
                            Log.d(TAG, Jobject.getString("balance_money"));
                            Log.d(TAG, Jobject.getString("balance_bonus"));

                            if(null != gmap){

                                Drawable circleDrawable = ContextCompat.getDrawable(c, R.drawable.ic_carousel);
                                markerIcon = getMarkerIconFromDrawable(Objects.requireNonNull(circleDrawable));

                                gmap.addMarker(new MarkerOptions()

                                        .position(new LatLng(Jobject.getDouble("card_id"),
                                                Jobject.getDouble("name")))

                                        .title(Jobject.getString("card_id"))
                                        .snippet(Jobject.getString("card_id"))
                                        .icon(markerIcon)
                                );
                            }

                        }

                    } catch (IOException | JSONException e) {
                        Toast.makeText(c,"Ошибка "+e,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}

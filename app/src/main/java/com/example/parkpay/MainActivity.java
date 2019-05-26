package com.example.parkpay;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String APP_PREFERENCES = "mysettings";
    private static final String APP_PREFERENCES_TOKEN ="Token";
    private static final String APP_PREFERENCES_PASSWORD ="Password";
    private static final String APP_PREFERENCES_LOGIN ="Login";
    private static final String TAG = "myLogs";
    private SharedPreferences settings;
    private BottomNavigationViewEx bottomNav;

    Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        TypefaceUtil
//                .overrideFont(getApplicationContext(),
//                        "SERIF",
//                        "font/roboto_regular.ttf");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //FirebaseApp.initializeApp(this);

        overridePendingTransition(0, 0);

        settings=getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        bottomNav = findViewById(R.id.bottom_navigation);



        bottomNav.setIconSize(25,25);
        bottomNav.setTextVisibility(false);
        bottomNav.enableAnimation(false);
        bottomNav.enableShiftingMode(false);
        bottomNav.enableItemShiftingMode(false);

        c=this;

        bottomNav.setOnNavigationItemSelectedListener(navListener);

        //I added this if statement to keep the selected fragment when rotating the device
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new CardFragment()).commit();
        }

        if(settings.contains(APP_PREFERENCES_TOKEN)){

            refreshToken();

        }
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.nav_cart:
                        selectedFragment = new CardFragment();
                        break;
                    case R.id.nav_map:
                        selectedFragment = new ParksFragment();
                        break;
                    case R.id.nav_camera:
                        selectedFragment = new CameraFragment();
                        break;
                    case R.id.nav_news:
                        selectedFragment = new NewsFragment();
                        break;
                    case R.id.nav_profile:
                        selectedFragment = new ProfileFragment();
                        break;
                }
                if(selectedFragment!=null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                }

                return true;
            };

    public void replaceFragments(Class fragmentClass) {
        Fragment fragment=null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragment!=null)
        {
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }

    }

    public void replaceFragmentCamera(Class fragmentClass) {
        Fragment fragment=null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragment!=null)
        {
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }

    }

    public static void saveArrayList(ArrayList<String> list, String key, SharedPreferences settings){
        SharedPreferences.Editor editor = settings.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    public static ArrayList<String> getArrayList(String key, SharedPreferences settings){
        Gson gson = new Gson();
        String json = settings.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    static boolean isOnline(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    private void refreshToken(){

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        try {
            json.put("login",settings.getString(APP_PREFERENCES_LOGIN,""));
            json.put("password",settings.getString(APP_PREFERENCES_PASSWORD,""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonString = json.toString();
        RequestBody body = RequestBody.create(JSON, jsonString);
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .post(body)
                .url("https://api.mobile.goldinnfish.com/login")
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

                        String jsonData = null;
                        if (response.body() != null) {
                            jsonData = response.body().string();
                        }

                        JSONObject Jobject = new JSONObject(jsonData);

                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(APP_PREFERENCES_TOKEN,Jobject.getString("token"));
                        editor.apply();

                    } catch (IOException | JSONException e) {

                        Log.d(TAG,"Ошибка: "+e);
                    }
                });
            }
        });
    }

//    @Override
//    public void onBackPressed() {
//        FragmentManager fm = getSupportFragmentManager();
//        OnBackPressedListener backPressedListener = null;
//        for (Fragment fragment: fm.getFragments()) {
//            if (fragment instanceof  OnBackPressedListener) {
//                backPressedListener = (OnBackPressedListener) fragment;
//                break;
//            }
//        }
//
//        if (backPressedListener != null) {
//            backPressedListener.onBackPressed();
//        } else {
//            super.onBackPressed();
//        }
//    }


//    @Override
//    public void onBackPressed() {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(c);
//        builder.setTitle("Закрыть приложение");
//        builder.setMessage(Html
//                .fromHtml("<font color='#000000'>Вы действительно хотите закрыть приложение?</font>"));
//        builder.setCancelable(false);
//        builder.setPositiveButton("Да",
//                (dialog, id) -> {
//
//                    //moveTaskToBack(true);
//                    System.runFinalizersOnExit(true);
//                    System.exit(0);
//                    finish();
//
//                });
//        builder.setNegativeButton("Нет",
//                (dialog, id) -> dialog.cancel());
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();
//        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
//        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#3F51B5"));
//        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackground(null);
//    }

    //    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(c);
//        builder.setTitle("Закрыть приложение");
//        builder.setMessage(Html
//                .fromHtml("<font color='#000000'>Вы действительно хотите закрыть приложение?</font>"));
//        builder.setCancelable(false);
//        builder.setPositiveButton("Да",
//                (dialog, id) -> {
//
//                    moveTaskToBack(true);
//                    System.runFinalizersOnExit(true);
//                    System.exit(0);
//
//                });
//        builder.setNegativeButton("Нет",
//                (dialog, id) -> dialog.cancel());
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();
//        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
//        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#3F51B5"));
//        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackground(null);
//    }
}
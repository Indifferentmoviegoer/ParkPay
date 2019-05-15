package com.example.parkpay;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.firebase.iid.FirebaseInstanceId;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ProfileFragment extends Fragment {

    private AppCompatImageView editProfile;
    private TextView name;
    private AppCompatImageView signOut;
    private TextView quantityVisits;
    private TextView bonus;
    private TextView card;
    private ImageView imageProfile;
    AppCompatImageView share;
    ProgressBar progressBarProfile;

    private ListView operationList;
    private ArrayList<String> operationName;
    private ArrayList<String> operationDate;
    private ArrayList<String> operationValue;

    private BonusAdapter bonusAdapter;

    // private PublisherAdRequest adRequest;

    private Context c;
    private static final String APP_PREFERENCES = "mysettings";
    private static final String APP_PREFERENCES_NOTIFICATION ="TURN_NOTIFICATION";
    private static final String APP_PREFERENCES_NAME ="Name";
    private static final String APP_PREFERENCES_TOKEN ="Token";
    private static final String APP_PREFERENCES_PHOTO ="Photo";
    private static final String APP_PREFERENCES_QUANTITY_VISITS ="quantityVisits";
    private static final String APP_PREFERENCES_BONUS ="bonus";
    private static final String APP_PREFERENCES_QUANTITY_CARD ="quantityCard";
    private SharedPreferences settings;
    // private Switch notification;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);

        editProfile= view.findViewById(R.id.editProfile);
        name= view.findViewById(R.id.nameProfile);
        //notification = view.findViewById(R.id.turnNotification);
        signOut= view.findViewById(R.id.signOut);
        quantityVisits= view.findViewById(R.id.quantityVisits);
        imageProfile= view.findViewById(R.id.imageProfile);
        share= view.findViewById(R.id.share);
        bonus= view.findViewById(R.id.bonus);
        card= view.findViewById(R.id.card);
        operationList= view.findViewById(R.id.operationList);
        progressBarProfile= view.findViewById(R.id.progressBarProfile);

        c=getContext();

        settings= Objects.requireNonNull(this.getActivity())
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        progressBarProfile.setVisibility(View.VISIBLE);
        operationList.setVisibility(View.INVISIBLE);

        share.setOnClickListener(view1 -> {
            sendInvite();
        });

        getHistory();

        if(settings.contains(APP_PREFERENCES_PHOTO))
        {
            Bitmap bit=decodeToBase64(settings.getString(APP_PREFERENCES_PHOTO,""));
            imageProfile.setImageBitmap(bit);
        }

        if(settings.contains(APP_PREFERENCES_QUANTITY_VISITS))
        {
            quantityVisits.setText(settings.getString(APP_PREFERENCES_QUANTITY_VISITS,""));
        }


//        MobileAds.initialize(c,"ca-app-pub-9760468716956149~8831006393");
//
//        PublisherAdView adView = view.findViewById(R.id.result);
//        adRequest = new PublisherAdRequest.Builder()
//                .addTestDevice("84572862AA21BA9B3431DAB0391D57C2")
//                .build();
//        adView.loadAd(adRequest);



        editProfile.setOnClickListener(v -> ((MainActivity) Objects.requireNonNull(getActivity()))
                .replaceFragments(EditProfileFragment.class));

        signOut.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(c);
            builder.setMessage("Вы действительно хотите выйти?");
            builder.setCancelable(false);
            builder.setPositiveButton("Да",
                    (dialog, id) -> {

                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(APP_PREFERENCES_TOKEN, null);
                        editor.apply();

                        Intent intent = new Intent(c,
                                SignInActivity.class);
                        startActivity(intent);

                        Objects.requireNonNull(getActivity()).finish();

                    });
            builder.setNegativeButton("Нет",
                    (dialog, id) -> dialog.cancel());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

//        if(settings.contains(APP_PREFERENCES_NOTIFICATION)) {
//            notification.setChecked(settings.getBoolean(APP_PREFERENCES_NOTIFICATION,
//                    false));
//        }

        if(settings.contains(APP_PREFERENCES_NAME)) {
            name.setText(settings.getString(APP_PREFERENCES_NAME,
                    ""));
        }

        if(settings.contains(APP_PREFERENCES_BONUS)) {
            bonus.setText(settings.getString(APP_PREFERENCES_BONUS,
                    ""));
        }

        if(settings.contains(APP_PREFERENCES_QUANTITY_CARD)) {
            card.setText(settings.getString(APP_PREFERENCES_QUANTITY_CARD,
                    ""));
        }

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = settings.edit();
//        editor.putBoolean(APP_PREFERENCES_NOTIFICATION,notification.isChecked());
        editor.putString(APP_PREFERENCES_NAME,name.getText().toString());
        editor.apply();

//        if(settings.contains(APP_PREFERENCES_NOTIFICATION)) {
//            notification.setChecked(settings.getBoolean(APP_PREFERENCES_NOTIFICATION,
//                    false));
//            if(settings.getBoolean(APP_PREFERENCES_NOTIFICATION,
//                    false))
//            {
//                Runnable runnable = () -> {
//                    try {
//                        FirebaseInstanceId.getInstance().deleteInstanceId();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                };
//                Thread thread = new Thread(runnable);
//                thread.start();
//            }
//            if(!settings.getBoolean(APP_PREFERENCES_NOTIFICATION,
//                    false)) {
//                FirebaseInstanceId.getInstance().getInstanceId();
//            }
//        }

    }

    private static Bitmap decodeToBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }


    private void sendInvite(){

        OkHttpClient client = new OkHttpClient();

        HttpUrl mySearchUrl = new HttpUrl.Builder()
                .scheme("https")
                .host("api.mobile.goldinnfish.com")
                .addPathSegment("stocks")
                .addPathSegment("invite")
                .build();

        Log.d(TAG,mySearchUrl.toString());

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

                            JSONObject Jobject = new JSONObject(jsonData);

                            Log.d(TAG, Jobject.getString("status"));
                            Log.d(TAG, Jobject.getString("msg"));


                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, Jobject.getString("msg"));
                            sendIntent.setType("text/plain");
                            startActivity(sendIntent);

                        } catch (IOException | JSONException e) {
                            Log.d(TAG, "Ошибка " + e);
                        }
                    });
                }
            }
        });
    }

    private void getHistory() {

        OkHttpClient client = new OkHttpClient();

        HttpUrl mySearchUrl = new HttpUrl.Builder()
                .scheme("https")
                .host("api.mobile.goldinnfish.com")
                .addPathSegment("report")
                .addPathSegment("stocks")
                .build();

        Log.d(TAG,mySearchUrl.toString());

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

                if (call.request().body() != null) {
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

                            operationName = new ArrayList<>();
                            operationDate = new ArrayList<>();
                            operationValue = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject Jobject = jsonArray.getJSONObject(i);

                                Log.d(TAG, Jobject.getString("title"));
                                Log.d(TAG, Jobject.getString("date"));
                                Log.d(TAG, Jobject.getString("bonus"));

                                operationName.add(Jobject.getString("title"));
                                operationDate.add(Jobject.getString("date"));
                                operationValue.add(Jobject.getString("bonus"));

                            }

                            bonusAdapter = new BonusAdapter(c, operationName, operationDate, operationValue);
                            bonusAdapter.notifyDataSetChanged();
                            operationList.setAdapter(bonusAdapter);


                            progressBarProfile.setVisibility(View.INVISIBLE);
                            operationList.setVisibility(View.VISIBLE);

                        } catch (IOException | JSONException e) {
                            Log.d(TAG, "Ошибка " + e);

                        }

                    });
                }
            }
        });
    }
}

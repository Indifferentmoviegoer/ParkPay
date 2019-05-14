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
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.firebase.iid.FirebaseInstanceId;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ProfileFragment extends Fragment {
    private TextView editProfile;
    private TextView name;
    private TextView signOut;
    private TextView quantityVisits;
    private ImageView imageProfile;
    AppCompatImageView share;

    private PublisherAdRequest adRequest;

    private Context c;
    private static final String APP_PREFERENCES = "mysettings";
    private static final String APP_PREFERENCES_NOTIFICATION ="TURN_NOTIFICATION";
    private static final String APP_PREFERENCES_NAME ="Name";
    private static final String APP_PREFERENCES_TOKEN ="Token";
    private static final String APP_PREFERENCES_PHOTO ="Photo";
    private static final String APP_PREFERENCES_QUANTITY_VISITS ="quantityVisits";
    private SharedPreferences settings;
    private Switch notification;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);

        editProfile= view.findViewById(R.id.editProfile);
        name= view.findViewById(R.id.nameProfile);
        notification = view.findViewById(R.id.turnNotification);
        signOut= view.findViewById(R.id.signOut);
        quantityVisits= view.findViewById(R.id.quantityVisits);
        imageProfile= view.findViewById(R.id.imageProfile);
        share= view.findViewById(R.id.share);

        c=getContext();

        settings= Objects.requireNonNull(this.getActivity())
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

//            Intent sendIntent = new Intent();
//            sendIntent.setAction(Intent.ACTION_SEND);
//            sendIntent.putExtra(Intent.EXTRA_TEXT, "273234");
//            sendIntent.setType("text/plain");
//            startActivity(sendIntent);


        share.setOnClickListener(view1 -> {
            sendInvite();
        });

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

        if(settings.contains(APP_PREFERENCES_NOTIFICATION)) {
            notification.setChecked(settings.getBoolean(APP_PREFERENCES_NOTIFICATION,
                    false));
        }

        if(settings.contains(APP_PREFERENCES_NAME)) {
            name.setText(settings.getString(APP_PREFERENCES_NAME,
                    ""));
        }

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(APP_PREFERENCES_NOTIFICATION,notification.isChecked());
        editor.putString(APP_PREFERENCES_NAME,name.getText().toString());
        editor.apply();

        if(settings.contains(APP_PREFERENCES_NOTIFICATION)) {
            notification.setChecked(settings.getBoolean(APP_PREFERENCES_NOTIFICATION,
                    false));
            if(settings.getBoolean(APP_PREFERENCES_NOTIFICATION,
                    false))
            {
                Runnable runnable = () -> {
                    try {
                        FirebaseInstanceId.getInstance().deleteInstanceId();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();
            }
            if(!settings.getBoolean(APP_PREFERENCES_NOTIFICATION,
                    false)) {
                FirebaseInstanceId.getInstance().getInstanceId();
            }
        }

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
}

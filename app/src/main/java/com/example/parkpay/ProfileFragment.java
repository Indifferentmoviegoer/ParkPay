package com.example.parkpay;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ProfileFragment extends Fragment {

    private TextView name;
    private AppCompatImageView share;
    private ProgressBar progressBarProfile;
    private AppCompatTextView noResult;

    private RecyclerView operationList;

    private List<Transaction> transactions;

    private TransactionAdapter transactionAdapter;

    // private PublisherAdRequest adRequest;

    private Context c;
    private static final String APP_PREFERENCES = "mysettings";
    //private static final String APP_PREFERENCES_NOTIFICATION ="TURN_NOTIFICATION";
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

        AppCompatImageView editProfile = view.findViewById(R.id.editProfile);
        name= view.findViewById(R.id.nameProfile);
        //notification = view.findViewById(R.id.turnNotification);
        AppCompatImageView signOut = view.findViewById(R.id.signOut);
        TextView quantityVisits = view.findViewById(R.id.quantityVisits);
        ImageView imageProfile = view.findViewById(R.id.imageProfile);
        share= view.findViewById(R.id.share);
        TextView bonus = view.findViewById(R.id.bonus);
        TextView card = view.findViewById(R.id.card);
        operationList= view.findViewById(R.id.operationList);
        progressBarProfile= view.findViewById(R.id.progressBarProfile);
        noResult= view.findViewById(R.id.noResult);

        c=getContext();

        settings= Objects.requireNonNull(this.getActivity())
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        progressBarProfile.setVisibility(View.VISIBLE);
        operationList.setVisibility(View.INVISIBLE);
        noResult.setVisibility(View.INVISIBLE);

        LinearLayoutManager llm = new LinearLayoutManager(c);
        operationList.setLayoutManager(llm);

        share.setOnClickListener(view1 -> {

            boolean checkConnection=MainActivity.isOnline(c);

            if(checkConnection) {

            sendInvite();

            } else {
                Toast.makeText(c, "Отсутствует интернет соединение!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        boolean checkConnection=MainActivity.isOnline(c);

        if(checkConnection) {

        getStocks();

        } else {

            progressBarProfile.setVisibility(View.INVISIBLE);
            operationList.setVisibility(View.VISIBLE);
            noResult.setText("Отсутствует интернет соединение!");
            noResult.setVisibility(View.VISIBLE);
        }

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
            builder.setTitle("Выйти");
            builder.setMessage(Html
                    .fromHtml("<font color='#000000'>Вы действительно хотите выйти?</font>"));
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
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#3F51B5"));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackground(null);
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

    }

    private static Bitmap decodeToBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }


    private void sendInvite(){

        OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder()
                .url("https://api.mobile.goldinnfish.com/stocks/invite")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
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

                            JSONObject Jobject = new JSONObject(Objects.requireNonNull(jsonData));

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

    private void getStocks() {

        OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder()
                .url("https://api.mobile.goldinnfish.com/report/stocks")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization","Bearer "+
                        Objects.requireNonNull(settings.getString(APP_PREFERENCES_TOKEN, "")))
                .get()
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

                            transactions= new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject Jobject = jsonArray.getJSONObject(i);

                                transactions.add(new Transaction(

                                Jobject.getString("title"),
                                Jobject.getString("date"),
                                Jobject.getString("bonus")+" Б",
                                R.drawable.bon

                                 ));

                            }

                            getTransactions();

                        } catch (IOException | JSONException e) {
                            Log.d(TAG, "Ошибка " + e);

                            noResult.setVisibility(View.VISIBLE);

                        }

                    });
                }
            }
        });
    }

    private void getTransactions() {

        OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder()
                .url("https://api.mobile.goldinnfish.com/report/transactions")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization","Bearer "+
                        Objects.requireNonNull(settings.getString(APP_PREFERENCES_TOKEN, "")))
                .get()
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

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject Jobject = jsonArray.getJSONObject(i);

                                if(Jobject.getString("status").contains("succeeded")) {

                                    transactions.add(new Transaction(

                                            "Пополнение карты " + Jobject.getString("code"),
                                            Jobject.getString("date"),
                                            Jobject.getString("value")+" \u20BD",
                                            R.drawable.ic_mon

                                    ));
                                }

                            }

                            Collections.sort(transactions, (o1, o2) -> o2.date.compareTo(o1.date));
                            transactionAdapter = new TransactionAdapter(c, transactions);
                            transactionAdapter.notifyDataSetChanged();
                            operationList.setAdapter(transactionAdapter);


                            progressBarProfile.setVisibility(View.INVISIBLE);
                            operationList.setVisibility(View.VISIBLE);

                            noResult.setVisibility(View.INVISIBLE);

                        } catch (IOException | JSONException e) {
                            Log.d(TAG, "Ошибка " + e);

                            progressBarProfile.setVisibility(View.INVISIBLE);
                            operationList.setVisibility(View.VISIBLE);
                            noResult.setVisibility(View.VISIBLE);

                        }

                    });
                }
            }
        });
    }
}

package com.example.parkpay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardsViewHolder>{

    private final List<Card> cards;
    private final Context context;

    private static final String APP_PREFERENCES = "mysettings";
    private static final String APP_PREFERENCES_CARDS ="Cards";
    private static final String APP_PREFERENCES_POSITION_CARD ="position";
    private static final String APP_PREFERENCES_POSITION_GROUP ="group";
    private static final String APP_PREFERENCES_TOKEN ="Token";

    private static final String APP_PREFERENCES_CARD_DELETE ="cardDelete";
    private static final String APP_PREFERENCES_CARD_NAME ="cardName";
    private static final String APP_PREFERENCES_CARD_CODE ="cardCode";
    private static final String TAG = "myLogs";

    private SharedPreferences settings;

    CardAdapter(Context context, List<Card> cards){
        this.cards = cards;
        this.context = context;
    }

    @NonNull
    @Override
    public CardsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_card, viewGroup, false);

        settings= Objects.requireNonNull(context)
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        return new CardsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CardsViewHolder cardsViewHolder, int i) {
        cardsViewHolder.nameCard.setText(cards.get(i).name);
        cardsViewHolder.codeCard.setText(cards.get(i).code);
        cardsViewHolder.moneyCard.setText(cards.get(i).money);
        cardsViewHolder.bonusCard.setText(cards.get(i).bonus);

//        Typeface tf = ResourcesCompat.getFont(context,
//                R.font.roboto_regular);
//        cardsViewHolder.nameCard.setTypeface(tf);

        cardsViewHolder.imageDelete.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                    builder.setView();
            builder.setTitle("Удаление карты");
            builder.setMessage(Html
                    .fromHtml("<font color='#000000'>Вы действительно хотите удалить данную карту?</font>"));
            builder.setCancelable(false);
            builder.setPositiveButton("Да",
                    (dialog, id) -> {

                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(APP_PREFERENCES_CARD_DELETE, cards.get(i).id);
                        editor.putString(APP_PREFERENCES_CARD_NAME, cards.get(i).name);
                        editor.putString(APP_PREFERENCES_CARD_CODE, cards.get(i).code);
                        editor.apply();

                        Intent i1 = new Intent(context, MainActivity.class);

                        boolean checkConnection = MainActivity.isOnline(context);

                        if(checkConnection) {

                            deleteCard();

                        }
                        else {
                            Toast.makeText(context, "Отсутствует интернет соединение!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
            builder.setNegativeButton("Нет",
                    (dialog, id) -> dialog.cancel());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#3F51B5"));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackground(null);
        });

        cardsViewHolder.imageEdit.setOnClickListener(v -> {

            SharedPreferences.Editor editor = settings.edit();
            editor.putString(APP_PREFERENCES_CARD_DELETE, cards.get(i).id);
            editor.putString(APP_PREFERENCES_CARD_NAME, cards.get(i).name);
            editor.putString(APP_PREFERENCES_CARD_CODE, cards.get(i).code);
            editor.apply();

            Intent intent = new Intent(context,
                    EditCardActivity.class);
            context.startActivity(intent);
        });

        cardsViewHolder.addMoney.setOnClickListener(v -> {

            SharedPreferences.Editor editor = settings.edit();
            editor.putString(APP_PREFERENCES_CARD_DELETE, cards.get(i).id);
            editor.putString(APP_PREFERENCES_CARD_NAME, cards.get(i).name);
            editor.putString(APP_PREFERENCES_CARD_CODE, cards.get(i).code);
            editor.apply();

            Intent intent = new Intent(context,
                    PayActivity.class);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NotNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class CardsViewHolder extends RecyclerView.ViewHolder {

        final TextView nameCard;
        final TextView codeCard;
        final TextView moneyCard;
        final TextView bonusCard;
        final ImageView imageDelete;
        final ImageView imageEdit;
        final ImageView addMoney;
        final ImageView imageCard;

        CardsViewHolder(View itemView) {
            super(itemView);

            nameCard = itemView.findViewById(R.id.nameCard);
            codeCard = itemView.findViewById(R.id.codeCard);
            moneyCard = itemView.findViewById(R.id.moneyCard);
            bonusCard = itemView.findViewById(R.id.bonusCard);
            imageDelete = itemView.findViewById(R.id.imageDelete);
            imageEdit = itemView.findViewById(R.id.imageEdit);
            addMoney = itemView.findViewById(R.id.addMoney);
            imageCard = itemView.findViewById(R.id.imageCard);
        }

    }

    private void deleteCard(){

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        try {
            json.put("card_id",settings.getString(APP_PREFERENCES_CARD_DELETE, ""));

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
                .url("https://api.mobile.goldinnfish.com/card/delete")
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                if(call.request().body()!=null)
                {
                    Log.d(TAG, Objects.requireNonNull(call.request().body()).toString());
                }

                ((Activity) context).runOnUiThread(() -> {
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) {
                ((Activity) context).runOnUiThread(() -> {
                    try {

                        String jsonData = null;
                        if (response.body() != null) {
                            jsonData = response.body().string();
                        }

                        JSONObject Jobject = new JSONObject(jsonData);

                        Intent i = new Intent(context, MainActivity.class);


                        if(Jobject.getString("status").equals("1"))

                        {

                            Toast
                                    .makeText(context,"Удаление карты",Toast.LENGTH_SHORT)
                                    .show();

                            context.startActivity(i);
                        }

                        if(Jobject.getString("status").equals("0")) {

                            Toast.makeText(context,
                                    Jobject.getString("msg"),
                                    Toast.LENGTH_SHORT).show();
                        }

                    } catch (IOException | JSONException e) {
                        Toast.makeText(context,"Ошибка "+e,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}
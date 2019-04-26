package com.example.parkpay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class CustomAdapter extends BaseAdapter {

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_CARDS ="Cards";
    public static final String APP_PREFERENCES_POSITION_CARD ="position";
    public static final String APP_PREFERENCES_POSITION_GROUP ="group";
    public static final String APP_PREFERENCES_TOKEN ="Token";

    public static final String APP_PREFERENCES_CARD_DELETE ="cardDelete";
    public static final String APP_PREFERENCES_CARD_NAME ="cardName";
    public static final String APP_PREFERENCES_CARD_CODE ="cardCode";
    private static final String TAG = "myLogs";

    SharedPreferences settings;

    private ArrayList<String> child;

    Context context;
    ArrayList<String> Item;
    ArrayList<String> SubItem;
    ArrayList<String> MoneyCard;
    ArrayList<String> BonusCard;
    ArrayList<String> CardId;
    int flags[];
    LayoutInflater inflter;

    public CustomAdapter(Context applicationContext, ArrayList<String> Item,
                         ArrayList<String> SubItem,ArrayList<String> MoneyCard,ArrayList<String> BonusCard,
                         ArrayList<String> CardId) {
        this.context = applicationContext;
        this.Item = Item;
        this.SubItem = SubItem;

        this.MoneyCard = MoneyCard;
        this.BonusCard = BonusCard;

        this.CardId = CardId;

        inflter = (LayoutInflater.from(applicationContext));

        settings= Objects.requireNonNull(context)
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public int getCount() {
        return Item.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflter.inflate(R.layout.list_real_item, null);

        TextView item = (TextView) view.findViewById(R.id.item);
        TextView subitem = (TextView) view.findViewById(R.id.subitem);

        TextView moneyCard = (TextView) view.findViewById(R.id.moneyCard);
        TextView bonusCard = (TextView) view.findViewById(R.id.bonusCard);

        ImageView imageDelete=(ImageView) view.findViewById(R.id.imageDelete);
        ImageView imageEdit=(ImageView) view.findViewById(R.id.imageEdit);

        Button addMoney=(Button) view.findViewById(R.id.addMoney);

        item.setText(Item.get(i));
        subitem.setText(SubItem.get(i));

        moneyCard.setText(MoneyCard.get(i));
        bonusCard.setText(BonusCard.get(i));

        //image.setImageResource(flags[i]);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (SubItem.get(i).contains("Номер карты")) {
//
//                    Intent intent = new Intent(context, AddCardActivity.class);
//                    context.startActivity(intent);
//
//                } else {

                    if (settings.contains(APP_PREFERENCES_CARDS)) {
                        child = MainActivity.getArrayList(APP_PREFERENCES_CARDS, settings);
                    }
                    Intent intent = new Intent(context, DetailCardActivity.class);

                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(APP_PREFERENCES_POSITION_CARD, child.get(i));
                    editor.putInt(APP_PREFERENCES_POSITION_GROUP, i);
                    editor.apply();

                    context.startActivity(intent);

//                }
            }
        });

        imageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(Html
                            .fromHtml("<font color='#000000'>Вы действительно хотите удалить данную карту?</font>"));
                    builder.setCancelable(false);
                    builder.setPositiveButton("Да",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putString(APP_PREFERENCES_CARD_DELETE,CardId.get(i));
                                    editor.putString(APP_PREFERENCES_CARD_NAME,Item.get(i));
                                    editor.putString(APP_PREFERENCES_CARD_CODE,SubItem.get(i));
                                    editor.apply();

                                    Intent i = new Intent(context, MainActivity.class);

                                    boolean checkConnection=MainActivity.isOnline(context);

//                                        if(checkConnection) {

                                    doPostRequest("http://192.168.252.199/card/delete");

//                                        }
//                                        else {
//                                            Toast.makeText(getApplicationContext(), "Отсутствует интернет соединение!",
//                                                    Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
                                }
                            });
                    builder.setNegativeButton("Нет",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
        });

        imageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = settings.edit();
                editor.putString(APP_PREFERENCES_CARD_DELETE,CardId.get(i));
                editor.putString(APP_PREFERENCES_CARD_NAME,Item.get(i));
                editor.putString(APP_PREFERENCES_CARD_CODE,SubItem.get(i));
                editor.apply();

                Intent intent = new Intent(context,
                        EditCardActivity.class);
                context.startActivity(intent);
            }
        });

        addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context,
                        PayActivity.class);
                context.startActivity(intent);
            }
        });

        return view;
    }

    public void doPostRequest(String url){

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

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                ((Activity) context).runOnUiThread(() -> {
                    try {

                        String jsonData = null;
                        if (response.body() != null) {
                            jsonData = response.body().string();
                        }

                        JSONObject Jobject = new JSONObject(jsonData);

                        Intent i = new Intent(context, MainActivity.class);

                        Log.d(TAG,Jobject.getString("status"));
                        Log.d(TAG,Jobject.getString("msg"));



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

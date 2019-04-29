package com.example.parkpay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ParksAdapter extends BaseAdapter {

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_TOKEN ="Token";

    public static final String APP_PREFERENCES_PARK_IDS ="parkIDs";
    public static final String APP_PREFERENCES_PARK_ID ="parkID";

    private static final String TAG = "myLogs";

    SharedPreferences settings;

    private ArrayList<String> child;

    Context context;
    ArrayList<String> Item;
    ArrayList<String> SubItem;
//    ArrayList<String> MoneyCard;
//    ArrayList<String> BonusCard;
//    ArrayList<String> CardId;
    int flags[];
    LayoutInflater inflter;

    public ParksAdapter(Context applicationContext,
                        ArrayList<String> Item,
                        ArrayList<String> SubItem
//                        ArrayList<String> MoneyCard,
//                        ArrayList<String> BonusCard,
//                        ArrayList<String> CardId
    ) {
        this.context = applicationContext;
        this.Item = Item;
        this.SubItem = SubItem;

//        this.MoneyCard = MoneyCard;
//        this.BonusCard = BonusCard;
//
//        this.CardId = CardId;

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

        view = inflter.inflate(R.layout.list_park_item, null);

//        LayoutInflater li = LayoutInflater.from(context);
//        View promptsView = li.inflate(R.layout.alert_dialog, null);

        TextView item = (TextView) view.findViewById(R.id.item);
        TextView subitem = (TextView) view.findViewById(R.id.subitem);

//        TextView moneyCard = (TextView) view.findViewById(R.id.moneyCard);
//        TextView bonusCard = (TextView) view.findViewById(R.id.bonusCard);
//
//        ImageView imageDelete=(ImageView) view.findViewById(R.id.imageDelete);
//        ImageView imageEdit=(ImageView) view.findViewById(R.id.imageEdit);
//
//        ImageView addMoney=(ImageView) view.findViewById(R.id.addMoney);

        item.setText(Item.get(i));
        subitem.setText(SubItem.get(i));

//        moneyCard.setText(MoneyCard.get(i));
//        bonusCard.setText(BonusCard.get(i));

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


                    if (settings.contains(APP_PREFERENCES_PARK_IDS)) {
                        child = MainActivity.getArrayList(APP_PREFERENCES_PARK_IDS, settings);
                    }
                    Intent intent = new Intent(context, Main2Activity.class);

                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(APP_PREFERENCES_PARK_ID, child.get(i));
                    editor.apply();

                    context.startActivity(intent);

//                }
            }
        });

        return view;
    }

}

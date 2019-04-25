package com.example.parkpay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

public class HistoryAdapter extends BaseAdapter {

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_CARDS ="Cards";
    public static final String APP_PREFERENCES_POSITION_CARD ="position";
    public static final String APP_PREFERENCES_POSITION_GROUP ="group";
    SharedPreferences settings;
    private ArrayList<String> child;

    Context context;
    ArrayList<String> Item;
    ArrayList<String> SubItem;
    ArrayList<String> MoneyCard;
    int flags[];
    LayoutInflater inflter;

    public HistoryAdapter(Context applicationContext, ArrayList<String> Item,
                          ArrayList<String> SubItem, ArrayList<String> MoneyCard) {
        this.context = applicationContext;
        this.Item = Item;
        this.SubItem = SubItem;

        this.MoneyCard = MoneyCard;
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

        view = inflter.inflate(R.layout.list_history_item, null);

        TextView item = (TextView) view.findViewById(R.id.item);
        TextView subitem = (TextView) view.findViewById(R.id.subitem);

        TextView moneyCard = (TextView) view.findViewById(R.id.value);

        item.setText(Item.get(i));
        subitem.setText(SubItem.get(i));

        if(Item.get(i).contains("Пополнение")){

            String plus="+"+MoneyCard.get(i);
            moneyCard.setText(plus);
            moneyCard.setTextColor(Color.parseColor("#8BC34A"));
        }

        else if(Item.get(i).contains("Проход")){

            String minus="-"+MoneyCard.get(i);
            moneyCard.setText(minus);
            moneyCard.setTextColor(Color.parseColor("#F44336"));
        }

        else if(Item.get(i).contains("Возврат средств")){

            String plus="+"+MoneyCard.get(i);
            moneyCard.setText(plus);
            moneyCard.setTextColor(Color.parseColor("#8BC34A"));
        }

        else {
            moneyCard.setText("");
        }

        return view;
    }
}

package com.example.parkpay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

class BonusAdapter extends BaseAdapter {

    private static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_CARDS ="Cards";
    public static final String APP_PREFERENCES_POSITION_CARD ="position";
    public static final String APP_PREFERENCES_POSITION_GROUP ="group";
    private final SharedPreferences settings;
    private ArrayList<String> child;

    private final Context context;
    private final ArrayList<String> Item;
    private final ArrayList<String> SubItem;
    private final ArrayList<String> MoneyCard;
    int[] flags;
    private final LayoutInflater inflter;

    public BonusAdapter(Context applicationContext, ArrayList<String> Item,
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

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflter.inflate(R.layout.list_bonus_item, null);

        TextView item = view.findViewById(R.id.item);
        TextView subitem = view.findViewById(R.id.subitem);
        TextView time = view.findViewById(R.id.time);

        TextView moneyCard = view.findViewById(R.id.value);

        item.setText(Item.get(i));

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        SimpleDateFormat formatterOut = new SimpleDateFormat("dd MMM");
        SimpleDateFormat timeValue = new SimpleDateFormat("HH:mm:ss");


        try {



            Date date = formatter.parse(SubItem.get(i));
            Date timeValues = formatter.parse(SubItem.get(i));

//            if(date.isEqual(historyDate) && todayDate.before(futureDate)) {
//                // In between
//            }

            subitem.setText(formatterOut.format(date));

            time.setText(timeValue.format(timeValues));

        } catch (ParseException e) {
            e.printStackTrace();
        }




        String plus="+"+MoneyCard.get(i);
        moneyCard.setText(plus);
        moneyCard.setTextColor(Color.parseColor("#8BC34A"));

//        if(Item.get(i).contains("Пополнение")){
//
//            String plus="+"+MoneyCard.get(i);
//            moneyCard.setText(plus);
//            moneyCard.setTextColor(Color.parseColor("#8BC34A"));
//        }
//
//        else if(Item.get(i).contains("Проход")){
//
//            String minus="-"+MoneyCard.get(i);
//            moneyCard.setText(minus);
//            moneyCard.setTextColor(Color.parseColor("#F44336"));
//        }
//
//        else if(Item.get(i).contains("Возврат средств")){
//
//            String plus="+"+MoneyCard.get(i);
//            moneyCard.setText(plus);
//            moneyCard.setTextColor(Color.parseColor("#8BC34A"));
//        }
//
//        else {
//            moneyCard.setText("");
//        }

        return view;
    }
}

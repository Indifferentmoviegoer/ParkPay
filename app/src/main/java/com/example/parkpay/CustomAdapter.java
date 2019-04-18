package com.example.parkpay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

public class CustomAdapter extends BaseAdapter {

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_CARDS ="Cards";
    public static final String APP_PREFERENCES_POSITION_CARD ="position";
    SharedPreferences settings;
    private ArrayList<String> child;

    Context context;
    ArrayList<String> Item;
    ArrayList<String> SubItem;
    int flags[];
    LayoutInflater inflter;

    public CustomAdapter(Context applicationContext, ArrayList<String> Item, ArrayList<String> SubItem) {
        this.context = applicationContext;
        this.Item = Item;
        this.SubItem = SubItem;
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
        //ImageView image = (ImageView) convertView.findViewById(R.id.image);
        item.setText(Item.get(i));
        subitem.setText(SubItem.get(i));

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
                    editor.apply();

                    context.startActivity(intent);

//                }
            }
        });

        return view;
    }
}

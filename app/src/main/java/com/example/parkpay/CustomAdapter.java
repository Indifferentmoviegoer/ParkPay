package com.example.parkpay;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Html;
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
    public static final String APP_PREFERENCES_POSITION_GROUP ="group";
    SharedPreferences settings;
    private ArrayList<String> child;

    Context context;
    ArrayList<String> Item;
    ArrayList<String> SubItem;
    ArrayList<String> MoneyCard;
    ArrayList<String> BonusCard;
    int flags[];
    LayoutInflater inflter;

    public CustomAdapter(Context applicationContext, ArrayList<String> Item,
                         ArrayList<String> SubItem,ArrayList<String> MoneyCard,ArrayList<String> BonusCard) {
        this.context = applicationContext;
        this.Item = Item;
        this.SubItem = SubItem;

        this.MoneyCard = MoneyCard;
        this.BonusCard = BonusCard;
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

//        ImageView imageDelete=(ImageView) view.findViewById(R.id.imageDelete);
//        ImageView imageEdit=(ImageView) view.findViewById(R.id.imageEdit);

        //ImageView image = (ImageView) convertView.findViewById(R.id.image);
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

//        imageDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                    builder.setMessage(Html
//                            .fromHtml("<font color='#000000'>Вы действительно хотите удалить данную карту?</font>"));
//                    builder.setCancelable(false);
//                    builder.setPositiveButton("Да",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//
//                                    Intent i = new Intent(c, MainActivity.class);
//
////                                    if(groupPosition==0){
//
//                                    if(settings.contains(APP_PREFERENCES_CARDS)){
//                                        child=MainActivity.getArrayList(APP_PREFERENCES_CARDS,settings);
//                                        children2=MainActivity.getArrayList(APP_PREFERENCES_NAMES_CARDS,settings);
//                                    }
//
//                                    boolean checkConnection=MainActivity.isOnline(c);
//
////                                        if(checkConnection) {
//
//                                    doPostRequest("http://192.168.252.199/card/delete");
//
////                                        }
////                                        else {
////                                            Toast.makeText(getApplicationContext(), "Отсутствует интернет соединение!",
////                                                    Toast.LENGTH_SHORT).show();
////                                        }
////                                    }
////                                    if(groupPosition==1){
////
////                                        if(settings.contains(APP_PREFERENCES_VIRTUAL_CARDS)){
////
////                                            children2=MainActivity.getArrayList(APP_PREFERENCES_VIRTUAL_CARDS,settings);
////                                        }
////
////                                        children2.remove(cardNumber);
////
////                                        MainActivity.saveArrayList(children2, APP_PREFERENCES_VIRTUAL_CARDS,settings);
////                                        startActivity(i);
////                                    }
//                                }
//                            });
//                    builder.setNegativeButton("Нет",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    dialog.cancel();
//                                }
//                            });
//                    AlertDialog alertDialog = builder.create();
//                    alertDialog.show();
//                }
//        });
//
//        imageEdit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(context,
//                        EditCardActivity.class);
//                context.startActivity(intent);
//            }
//        });

        return view;
    }
}

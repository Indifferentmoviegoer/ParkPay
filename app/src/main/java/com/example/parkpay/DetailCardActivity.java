package com.example.parkpay;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

public class DetailCardActivity extends AppCompatActivity {

    TextView numberCard;
    String number;
    Button deleteCard;
    Button payCard;
    Context c;
    SharedPreferences settings;
    int groupPosition;
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_CARDS ="Cards";
    public static final String APP_PREFERENCES_VIRTUAL_CARDS ="virtualCards";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_card);

        c=this;
        numberCard=(TextView)findViewById(R.id.numberCard) ;
        deleteCard=(Button)findViewById(R.id.deleteCard);
        payCard=(Button)findViewById(R.id.payCard);
        settings= Objects.requireNonNull(c)
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                number= null;
                groupPosition= 10;
            } else {
                number= extras.getString("POSITION_CARD");
                groupPosition= extras.getInt("POSITION_GROUP");
            }
        } else {
            number= (String) savedInstanceState.getSerializable("POSITION_CARD");
            groupPosition= (int) savedInstanceState.getSerializable("POSITION_GROUP");
        }
        if(number!=null){
            numberCard.setText(number);
        }

        deleteCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(number!=null){

                    AlertDialog.Builder builder = new AlertDialog.Builder(c);
                    builder.setMessage("Вы действительно хотите удалить данную карту?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Да",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    Intent i = new Intent(c, MainActivity.class);

                                    ArrayList<String> children1 = new ArrayList<String>();
                                    ArrayList<String> children2 = new ArrayList<String>();
                                    if(groupPosition==0){
                                        if(settings.contains(APP_PREFERENCES_CARDS)){
                                            children1=MainActivity.getArrayList(APP_PREFERENCES_CARDS,settings);
                                        }
                                        children1.remove(number);
                                        MainActivity.saveArrayList(children1, APP_PREFERENCES_CARDS,settings);
                                        startActivity(i);
                                    }
                                    if(groupPosition==1){
                                        if(settings.contains(APP_PREFERENCES_VIRTUAL_CARDS)){
                                            children2=MainActivity.getArrayList(APP_PREFERENCES_VIRTUAL_CARDS,settings);
                                        }
                                        children2.remove(number);
                                        MainActivity.saveArrayList(children2, APP_PREFERENCES_VIRTUAL_CARDS,settings);
                                        startActivity(i);
                                    }
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
            }
        });

        payCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(c,
                        PayActivity.class);
                startActivity(intent);
            }
        });
    }
}

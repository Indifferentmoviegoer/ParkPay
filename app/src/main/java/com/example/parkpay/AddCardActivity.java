package com.example.parkpay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

public class AddCardActivity extends AppCompatActivity {

    Button buttonAddCard;
    EditText numberAddCard;
    Context c;
    SharedPreferences settings;
    int groupPosition;
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_СARD ="Card";
    public static final String APP_PREFERENCES_CARDS ="Cards";
    public static final String APP_PREFERENCES_VIRTUAL_CARDS ="virtualCards";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        c=this;
        buttonAddCard=(Button)findViewById(R.id.buttonAddCard);
        numberAddCard=(EditText)findViewById(R.id.numberAddCard);
        settings= Objects.requireNonNull(c)
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        if(settings.contains(APP_PREFERENCES_СARD)){
            numberAddCard.setText(settings.getString(APP_PREFERENCES_СARD, ""));
        }

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                groupPosition= 10;
            } else {
                groupPosition= extras.getInt("POSITION_GROUP");
            }
        } else {
            groupPosition= (int) savedInstanceState.getSerializable("POSITION_GROUP");
        }

        buttonAddCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(c, MainActivity.class);
                ArrayList<String> children1 = new ArrayList<String>();
                ArrayList<String> children2 = new ArrayList<String>();

                if(groupPosition==0){
                    if(settings.contains(APP_PREFERENCES_CARDS)){
                        children1=MainActivity.getArrayList(APP_PREFERENCES_CARDS,settings);
                    }
                    if(numberAddCard.getText().toString().equals("") || numberAddCard.getText().toString().length() == 0){
                        Toast.makeText(getApplicationContext(), "Заполните все поля ввода!",
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        children1.add(numberAddCard.getText().toString());
                        MainActivity.saveArrayList(children1, APP_PREFERENCES_CARDS,settings);
                        startActivity(i);
                    }
                }
                if(groupPosition==1){
                    if(settings.contains(APP_PREFERENCES_VIRTUAL_CARDS)){
                        children2=MainActivity.getArrayList(APP_PREFERENCES_VIRTUAL_CARDS,settings);
                    }
                    if(numberAddCard.getText().toString().equals("") || numberAddCard.getText().toString().length() == 0){
                        Toast.makeText(getApplicationContext(), "Заполните все поля ввода!",
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        children2.add(numberAddCard.getText().toString());
                        MainActivity.saveArrayList(children2, APP_PREFERENCES_VIRTUAL_CARDS,settings);
                        startActivity(i);
                    }
                }
            }
        });

        numberAddCard.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (numberAddCard.getRight() - numberAddCard.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        FragmentCamera.scanQR(v,AddCardActivity.this);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(settings.contains(APP_PREFERENCES_СARD)){
            numberAddCard.setText(settings.getString(APP_PREFERENCES_СARD, ""));
        }
    }
}

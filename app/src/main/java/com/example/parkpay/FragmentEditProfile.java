package com.example.parkpay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class FragmentEditProfile extends Fragment {
    EditText name;
    EditText fam;
    EditText ot;
    EditText phone;
    EditText email;
    EditText pass;
    EditText numberCard;
    Button save;
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_NAME ="Name";
    public static final String APP_PREFERENCES_FAM="Fam";
    public static final String APP_PREFERENCES_OTCH ="Otch";
    public static final String APP_PREFERENCES_NUMBER ="Number";
    public static final String APP_PREFERENCES_MAIL ="Email";
    public static final String APP_PREFERENCES_PASS ="Password";
    SharedPreferences settings;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_edit_profile,container,false);
        save=(Button) view.findViewById(R.id.save);
        name=(EditText) view.findViewById(R.id.name);
        fam=(EditText) view.findViewById(R.id.fam);
        ot=(EditText) view.findViewById(R.id.otc);
        phone=(EditText) view.findViewById(R.id.number);
        email=(EditText) view.findViewById(R.id.mail);
        pass=(EditText) view.findViewById(R.id.passw);
        settings= Objects.requireNonNull(this.getActivity())
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(APP_PREFERENCES_NAME,name.getText().toString());
                editor.putString(APP_PREFERENCES_FAM,fam.getText().toString());
                editor.putString(APP_PREFERENCES_OTCH,ot.getText().toString());
                editor.putString(APP_PREFERENCES_NUMBER,phone.getText().toString());
                editor.putString(APP_PREFERENCES_MAIL,email.getText().toString());
                editor.putString(APP_PREFERENCES_PASS,pass.getText().toString());
                editor.apply();
                ((MainActivity) Objects.requireNonNull(getActivity()))
                        .replaceFragments(FragmentProfile.class);
            }
        });

        if(settings.contains(APP_PREFERENCES_NAME)&&settings.contains(APP_PREFERENCES_FAM)
                &&settings.contains(APP_PREFERENCES_OTCH)&&settings.contains(APP_PREFERENCES_NUMBER)
                &&settings.contains(APP_PREFERENCES_MAIL)&&settings.contains(APP_PREFERENCES_PASS)) {
            name.setText(settings.getString(APP_PREFERENCES_NAME, ""));
            fam.setText(settings.getString(APP_PREFERENCES_FAM, ""));
            ot.setText(settings.getString(APP_PREFERENCES_OTCH, ""));
            phone.setText(settings.getString(APP_PREFERENCES_NUMBER, ""));
            email.setText(settings.getString(APP_PREFERENCES_MAIL, ""));
            pass.setText(settings.getString(APP_PREFERENCES_PASS, ""));
        }
        return view;
    }
}

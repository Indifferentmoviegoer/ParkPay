package com.example.parkpay;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class ProfileFragment extends Fragment {
    TextView editProfile;
    TextView name;
    TextView signOut;
    Context c;
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_NOTIFICATION ="TURN_NOTIFICATION";
    public static final String APP_PREFERENCES_NAME ="Name";
    public static final String APP_PREFERENCES_TOKEN ="Token";
    SharedPreferences settings;
    Switch notification;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);

        editProfile=(TextView)view.findViewById(R.id.editProfile);
        name=(TextView)view.findViewById(R.id.nameProfile);
        notification = (Switch) view.findViewById(R.id.turnNotification);
        signOut=(TextView) view.findViewById(R.id.signOut);
        c=getContext();

        settings= Objects.requireNonNull(this.getActivity())
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) Objects.requireNonNull(getActivity()))
                        .replaceFragments(EditProfileFragment.class);
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(c);
                builder.setMessage("Вы действительно хотите выйти?");
                builder.setCancelable(false);
                builder.setPositiveButton("Да",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString(APP_PREFERENCES_TOKEN,null);
                                editor.apply();

                                Intent intent = new Intent(c,
                                        SignInActivity.class);
                                startActivity(intent);

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

        if(settings.contains(APP_PREFERENCES_NOTIFICATION)) {
            notification.setChecked(settings.getBoolean(APP_PREFERENCES_NOTIFICATION,
                    false));
        }

        if(settings.contains(APP_PREFERENCES_NAME)) {
            name.setText(settings.getString(APP_PREFERENCES_NAME,
                    ""));
        }

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(APP_PREFERENCES_NOTIFICATION,notification.isChecked());
        editor.putString(APP_PREFERENCES_NAME,name.getText().toString());
        editor.apply();

        if(settings.contains(APP_PREFERENCES_NOTIFICATION)) {
            notification.setChecked(settings.getBoolean(APP_PREFERENCES_NOTIFICATION,
                    false));
            if(settings.getBoolean(APP_PREFERENCES_NOTIFICATION,
                    false))
            {
                Runnable runnable = new Runnable() {
                    public void run() {
                        try {
                            FirebaseInstanceId.getInstance().deleteInstanceId();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();
            }
            if(!settings.getBoolean(APP_PREFERENCES_NOTIFICATION,
                    false)) {
                FirebaseInstanceId.getInstance().getInstanceId();
            }
        }

    }
}

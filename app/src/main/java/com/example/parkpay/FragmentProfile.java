package com.example.parkpay;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.util.Objects;

public class FragmentProfile extends Fragment {
    TextView editProfile;
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_NOTIFICATION ="TURN_NOTIFICATION";
    SharedPreferences settings;
    Switch notification;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        editProfile=(TextView)view.findViewById(R.id.editProfile);
        notification = (Switch) view.findViewById(R.id.turnNotification);
        settings= Objects.requireNonNull(this.getActivity())
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        //settings= PreferenceManager.getDefaultSharedPreferences(getActivity());
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) Objects.requireNonNull(getActivity()))
                        .replaceFragments(FragmentEditProfile.class);
            }
        });
        if(settings.contains(APP_PREFERENCES_NOTIFICATION)) {
            notification.setChecked(settings.getBoolean(APP_PREFERENCES_NOTIFICATION,
                    false));
        }

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(APP_PREFERENCES_NOTIFICATION,notification.isChecked() );
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

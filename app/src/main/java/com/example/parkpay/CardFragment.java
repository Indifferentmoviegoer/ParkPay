package com.example.parkpay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CardFragment extends Fragment {

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_CARDS ="Cards";
    public static final String APP_PREFERENCES_NAMES_CARDS ="namesCards";
    public static final String APP_PREFERENCES_VIRTUAL_CARDS ="virtualCards";
    public static final String APP_PREFERENCES_TOKEN ="Token";
    public static final String APP_PREFERENCES_NAME ="Name";
    public static final String APP_PREFERENCES_NUMBER ="Number";
    public static final String APP_PREFERENCES_MAIL ="Email";
    public static final String APP_PREFERENCES_DATE_BIRTHDAY ="DateBirthday";
    public static final String APP_PREFERENCES_STATUS ="Status";
    private static final String TAG = "myLogs";

//    ImageView updateCard;

    TabLayout tabLayout;
    TabItem tabChats;
    TabItem tabStatus;
    ViewPager viewPager;
    PageAdapter pageAdapter;


    SharedPreferences settings;
    Context c;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_card,container,false);

        if (container != null) {
            c = container.getContext();
        }

        tabLayout = view.findViewById(R.id.switchFragment);
        tabChats = view.findViewById(R.id.realCardFragment);
        tabStatus = view.findViewById(R.id.virtualCardFragment);
        viewPager = view.findViewById(R.id.viewPager);

        pageAdapter = new PageAdapter(getChildFragmentManager(),
                tabLayout.getTabCount());

        viewPager.setAdapter(pageAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//        viewPager.add(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

//                if (tab.getPosition() == 1) {

                    viewPager.setCurrentItem(tab.getPosition());
//                    tabLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this,
//                            R.color.colorAccent));

//                } else if (tab.getPosition() == 2) {

//                    tabLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this,
//                            android.R.color.darker_gray));

//                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

//        updateCard=(ImageView)view.findViewById(R.id.updateCard);

        settings= Objects.requireNonNull(this.getActivity())
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

//        updateCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                SharedPreferences.Editor editor = settings.edit();
//                editor.remove(APP_PREFERENCES_NAMES_CARDS);
//                editor.remove(APP_PREFERENCES_CARDS);
//                editor.remove(APP_PREFERENCES_VIRTUAL_CARDS);
//                editor.apply();
//
//                children1.clear();
//                children2.clear();
//
//                Toast.makeText(c,"Обновление",Toast.LENGTH_SHORT).show();
//
//                doGetRequest();
//
//            }
//        });

        return view;
    }

}

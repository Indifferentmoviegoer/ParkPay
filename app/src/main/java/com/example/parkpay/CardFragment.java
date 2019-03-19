package com.example.parkpay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

public class CardFragment extends Fragment {

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_CARDS ="Cards";
    public static final String APP_PREFERENCES_VIRTUAL_CARDS ="virtualCards";
    SharedPreferences settings;
    Context c;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_card,container,false);
        if (container != null) {
            c = container.getContext();
        }
        // Находим наш list
        ExpandableListView listView = (ExpandableListView)view.findViewById(R.id.exListView);
        settings= Objects.requireNonNull(this.getActivity())
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        //Создаем набор данных для адаптера
        ArrayList<ArrayList<String>> groups = new ArrayList<ArrayList<String>>();
        ArrayList<String> children1 = new ArrayList<String>();
        ArrayList<String> children2 = new ArrayList<String>();
        if(settings.contains(APP_PREFERENCES_CARDS)){
            children1=getArrayList(APP_PREFERENCES_CARDS);
        }
        children1.add("Новая карта");
        groups.add(children1);

        if(settings.contains(APP_PREFERENCES_VIRTUAL_CARDS)){
            children2=getArrayList(APP_PREFERENCES_VIRTUAL_CARDS);
        }
        children2.add("Новая карта");
        groups.add(children2);
        //Создаем адаптер и передаем context и список с данными
        ExpListAdapter adapter = new ExpListAdapter(this.getActivity(), groups);
        listView.setAdapter(adapter);
        return view;
    }



    public void saveArrayList(ArrayList<String> list, String key){
        SharedPreferences.Editor editor = settings.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public ArrayList<String> getArrayList(String key){
        Gson gson = new Gson();
        String json = settings.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }
}

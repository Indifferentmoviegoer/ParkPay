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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NewsFragment extends Fragment {

    private List<State> states = new ArrayList();
    ListView countriesList;
    Context c;

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_CARD ="Card";
    SharedPreferences settings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_news,container,false);
        settings= Objects.requireNonNull(this.getActivity())
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        c=getContext();

        // начальная инициализация списка
        setInitialData();
        // получаем элемент ListView
        countriesList = (ListView) view.findViewById(R.id.countriesList);
        // создаем адаптер
        StateAdapter stateAdapter = new StateAdapter(c, R.layout.list_item, states);
        // устанавливаем адаптер
        countriesList.setAdapter(stateAdapter);
        // слушатель выбора в списке
        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                // получаем выбранный пункт
                State selectedState = (State)parent.getItemAtPosition(position);
                Toast.makeText(c, "Был выбран пункт " + selectedState.getName(),
                        Toast.LENGTH_SHORT).show();
            }
        };
        countriesList.setOnItemClickListener(itemListener);

        return view;
    }

    private void setInitialData(){

        states.add(new State ("Бразилия", "Бразилиа", R.drawable.card));
        states.add(new State ("Аргентина", "Буэнос-Айрес", R.drawable.card));
        states.add(new State ("Колумбия", "Богота", R.drawable.card));
        states.add(new State ("Уругвай", "Монтевидео", R.drawable.card));
        states.add(new State ("Чили", "Сантьяго", R.drawable.card));
    }

}
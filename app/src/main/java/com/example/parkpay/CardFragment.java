package com.example.parkpay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.Objects;

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
    ImageView addCard;

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
        addCard=(ImageView)view.findViewById(R.id.addCard);

        pageAdapter = new PageAdapter(getChildFragmentManager(),
                tabLayout.getTabCount());

        viewPager.setAdapter(pageAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.getSelectedTabPosition();

        //tabLayout.setupWithViewPager(viewPager);

        addCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(c, AddCardActivity.class);
                    c.startActivity(intent);
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                    viewPager.setCurrentItem(tab.getPosition());

                    addCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (tab.getPosition() == 0) {
                                Intent intent = new Intent(c, AddCardActivity.class);
                                c.startActivity(intent);
                            }
                            else if (tab.getPosition() == 1) {
                                Intent intent = new Intent(c, AddVirtualCardActivity.class);
                                c.startActivity(intent);
                            }

                        }
                    });
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

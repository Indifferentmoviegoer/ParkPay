package com.example.parkpay;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.ImageView;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class PayActivity extends AppCompatActivity {


    private Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        TabLayout tabLayout = findViewById(R.id.switchFragment);
        ViewPager viewPager = findViewById(R.id.viewPager);
        ImageView backPay = findViewById(R.id.backPay);

        c = this;


        PayAdapter payAdapter = new PayAdapter(getSupportFragmentManager(),
                tabLayout.getTabCount());

        viewPager.setAdapter(payAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.getSelectedTabPosition();

        backPay.setOnClickListener(v -> {

            Intent intent = new Intent(c,
                    MainActivity.class);
            startActivity(intent);

        });

    }

}

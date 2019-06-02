package com.example.parkpay;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.jetbrains.annotations.NotNull;

class PayAdapter extends FragmentPagerAdapter {

    private final int numOfTabs;

    PayAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PayFragment();
            case 1:
                return new PayBonusFragment();
            default:
                return new PayFragment();
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}

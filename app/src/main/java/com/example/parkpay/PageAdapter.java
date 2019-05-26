package com.example.parkpay;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.jetbrains.annotations.NotNull;

public class PageAdapter extends FragmentPagerAdapter {

    private final int numOfTabs;

    PageAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new CardFragment();
            case 1:
                return new CardFragment();
            default:
                return new CardFragment();
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}

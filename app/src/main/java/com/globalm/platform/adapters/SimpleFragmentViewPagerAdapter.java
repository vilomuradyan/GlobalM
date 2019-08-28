package com.globalm.platform.adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Pair;

import java.util.List;

public class SimpleFragmentViewPagerAdapter extends FragmentPagerAdapter {

    private List<Pair<String, Fragment>> fragments;

    public SimpleFragmentViewPagerAdapter(FragmentManager fm, List<Pair<String, Fragment>> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i).second;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragments.get(position).first;
    }
}

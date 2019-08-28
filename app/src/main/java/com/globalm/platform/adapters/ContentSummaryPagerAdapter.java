package com.globalm.platform.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.globalm.platform.R;
import com.globalm.platform.fragments.ListViewFragment;
import com.globalm.platform.fragments.MapViewFragment;

public class ContentSummaryPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public ContentSummaryPagerAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ListViewFragment();

            case 1:
                return new MapViewFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.list_view);

            case 1:
                return mContext.getString(R.string.map_view);

            default:
                return null;
        }
    }
}
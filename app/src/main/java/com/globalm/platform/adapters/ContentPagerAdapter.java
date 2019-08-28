package com.globalm.platform.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.globalm.platform.R;
import java.util.List;

public class ContentPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private List<Fragment> fragments;

    public ContentPagerAdapter(
            FragmentManager fragmentManager,
            Context context,
            List<Fragment> fragments) {
        super(fragmentManager);
        mContext = context;
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        if (position <= fragments.size() - 1) {
            return fragments.get(position);
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.plan_event);
            case 1:
                return mContext.getString(R.string.upload_file);
            default:
                return null;
        }
    }
}
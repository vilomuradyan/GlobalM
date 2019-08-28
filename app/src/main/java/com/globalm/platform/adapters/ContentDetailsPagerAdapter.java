package com.globalm.platform.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.globalm.platform.R;
import com.globalm.platform.fragments.ContentHistoryFragment;
import com.globalm.platform.fragments.DetailsFragment;
import com.globalm.platform.models.Item;

public class ContentDetailsPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private Item model;

    public ContentDetailsPagerAdapter(FragmentManager fragmentManager, Context context, Item model) {
        super(fragmentManager);
        mContext = context;
        this.model = model;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                DetailsFragment detailsFragment = new DetailsFragment();
                detailsFragment.setModel(model);
                return detailsFragment;
            case 1:
                return new ContentHistoryFragment();
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
                return mContext.getString(R.string.details);
            case 1:
                return mContext.getString(R.string.history);
            default:
                return null;
        }
    }
}
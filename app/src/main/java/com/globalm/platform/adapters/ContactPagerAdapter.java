package com.globalm.platform.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.globalm.platform.R;
import com.globalm.platform.fragments.ContactDetailsFragment;
import com.globalm.platform.fragments.ContactReviewsFragment;

public class ContactPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public ContactPagerAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ContactDetailsFragment();

            case 1:
                return new ContactReviewsFragment();
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
                return mContext.getString(R.string.reviews);

            default:
                return null;
        }
    }
}
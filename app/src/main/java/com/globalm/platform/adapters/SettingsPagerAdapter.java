package com.globalm.platform.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.globalm.platform.R;
import com.globalm.platform.fragments.AccountFragment;
import com.globalm.platform.fragments.TechnicalPreferencesFragment;

public class SettingsPagerAdapter extends FragmentPagerAdapter {
    private static final int ACCOUNT_PAGE_INDEX = 1;
    private static final int TECHNICAL_PREFS_PAGE_INDEX = 0;
    private Context mContext;

    public SettingsPagerAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case ACCOUNT_PAGE_INDEX:
                return new AccountFragment();

            case TECHNICAL_PREFS_PAGE_INDEX:
                return new TechnicalPreferencesFragment();
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
            case ACCOUNT_PAGE_INDEX:
                return mContext.getString(R.string.account);

            case TECHNICAL_PREFS_PAGE_INDEX:
                return mContext.getString(R.string.technical_preferences);

            default:
                return null;
        }
    }
}
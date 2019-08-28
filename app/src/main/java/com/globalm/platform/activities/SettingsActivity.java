package com.globalm.platform.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import com.globalm.platform.R;
import com.globalm.platform.adapters.SettingsPagerAdapter;

public class SettingsActivity extends BaseActivity {

    private TabLayout mTabLayoutSettings;
    private ViewPager mViewPagerSettings;
    private SettingsPagerAdapter mSettingsPagerAdapter;
    private ImageView mButtonBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#0A5ECC"));
        setContentView(R.layout.activity_settings);
        mTabLayoutSettings = findViewById(R.id.tab_layout_settings);
        mViewPagerSettings = findViewById(R.id.view_pager_settings);
        mButtonBack = findViewById(R.id.button_back);
        mSettingsPagerAdapter = new SettingsPagerAdapter(getSupportFragmentManager(), this);
        mViewPagerSettings.setOffscreenPageLimit(2);
        mViewPagerSettings.setAdapter(mSettingsPagerAdapter);
        mTabLayoutSettings.setupWithViewPager(mViewPagerSettings);
        Intent intent = getIntent();
        if (intent != null && intent.getStringExtra("account") != null && intent.getStringExtra("account").equals("account")){
            mViewPagerSettings.setCurrentItem(1);
        }else {
            mViewPagerSettings.setCurrentItem(0);
        }
        mButtonBack.setOnClickListener(v -> onBackPressed());
    }
}

package com.globalm.platform.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.globalm.platform.R;
import com.globalm.platform.adapters.ContactPagerAdapter;

public class ContactActivity extends BaseActivity {

    private TabLayout mTabLayoutContact;
    private ViewPager mViewPagerContact;
    private ContactPagerAdapter mContactPagerAdapter;
    private ImageView mButtonBack;
    private TextView mTextName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#0A5ECC"));
        setContentView(R.layout.activity_contact);
        mTabLayoutContact = findViewById(R.id.tab_layout_contact);
        mViewPagerContact = findViewById(R.id.view_pager_contact);
        mButtonBack = findViewById(R.id.button_back);
        mTextName = findViewById(R.id.text_name);
        mContactPagerAdapter = new ContactPagerAdapter(getSupportFragmentManager(), this);
        mViewPagerContact.setOffscreenPageLimit(2);
        mViewPagerContact.setAdapter(mContactPagerAdapter);
        mTabLayoutContact.setupWithViewPager(mViewPagerContact);
        mButtonBack.setOnClickListener(v -> onBackPressed());
    }
}
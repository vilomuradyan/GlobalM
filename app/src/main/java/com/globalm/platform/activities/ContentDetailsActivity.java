package com.globalm.platform.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.globalm.platform.R;
import com.globalm.platform.adapters.ContentDetailsPagerAdapter;
import com.globalm.platform.models.Item;

import java.io.Serializable;

public class ContentDetailsActivity extends BaseActivity {
    public static final String EXTRA_MODEL = "EXTRA_MODEL";

    private TabLayout mTabLayoutContentDetails;
    private ViewPager mViewPagerContentDetails;
    private ContentDetailsPagerAdapter mContentDetailsPagerAdapter;
    private ImageView mButtonBack;

    public static void start(Context context, Item model) {
        Intent i = new Intent(context, ContentDetailsActivity.class);
        i.putExtra(EXTRA_MODEL, model);
        context.startActivity(i);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.color_main_blue));
        setContentView(R.layout.activity_content_details);
        mTabLayoutContentDetails = findViewById(R.id.tab_layout_content);
        mViewPagerContentDetails = findViewById(R.id.view_pager_content);
        mButtonBack = findViewById(R.id.button_back);
        Serializable model = getIntent().getSerializableExtra(EXTRA_MODEL);
        mContentDetailsPagerAdapter = new ContentDetailsPagerAdapter(getSupportFragmentManager(), this, model instanceof Item ? (Item) model : null);
        mViewPagerContentDetails.setOffscreenPageLimit(2);
        mViewPagerContentDetails.setAdapter(mContentDetailsPagerAdapter);
        mTabLayoutContentDetails.setupWithViewPager(mViewPagerContentDetails);
        mButtonBack.setOnClickListener(v -> onBackPressed());
    }
}
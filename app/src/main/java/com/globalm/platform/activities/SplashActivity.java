package com.globalm.platform.activities;

import android.content.Intent;
import android.graphics.drawable.ClipDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.globalm.platform.R;

public class SplashActivity extends BaseActivity {

    private static final int CLIP_LEVEL_STEP = 100;
    private static final int CLIP_LEVEL_LIMIT = 10000;
    private static final int CLIP_LEVEL_STEP_DELAY = 10;

    private int mLevel = 0;
    private ClipDrawable mClipDrawable;
    private Handler mHandler = new Handler();
    private Runnable mRunnableAnimateImage = this::doTheAnimation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView imageLogo = findViewById(R.id.image_logo);
        mClipDrawable = (ClipDrawable) imageLogo.getDrawable();
        mClipDrawable.setLevel(0);
        mHandler.post(mRunnableAnimateImage);
    }

    private void doTheAnimation() {
        mLevel += CLIP_LEVEL_STEP;
        mClipDrawable.setLevel(mLevel);
        if (mLevel <= CLIP_LEVEL_LIMIT) {
            mHandler.postDelayed(mRunnableAnimateImage, CLIP_LEVEL_STEP_DELAY);
        } else {
            mHandler.removeCallbacks(mRunnableAnimateImage);
            startActivity(new Intent(SplashActivity.this, userDataManager.isUserLoggedIn() ? MainActivity.class : LoginActivity.class));
            finish();
        }
    }
}

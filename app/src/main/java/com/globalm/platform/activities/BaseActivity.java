package com.globalm.platform.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.globalm.platform.GlobalmApplication;
import com.globalm.platform.R;
import com.globalm.platform.managers.IUserDataManager;
import com.globalm.platform.managers.impl.UserDataManager;

public abstract class BaseActivity extends AppCompatActivity {

    protected IUserDataManager userDataManager;
    private boolean mConnectionLostOnce;

    private BroadcastReceiver mConnectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (GlobalmApplication.getInstance().isNetworkAvailable() && mConnectionLostOnce) {
                onConnectionRestored();
            }

            if (!GlobalmApplication.getInstance().isNetworkAvailable()) {
                onConnectionLost();
            }
        }
    };

    public void onConnectionLost() {
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Connection lost", Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.color_red));
        snackbar.show();
        mConnectionLostOnce = true;
    }

    public void onConnectionRestored() {
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Connection restored", Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.color_green));
        snackbar.show();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userDataManager = UserDataManager.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mConnectionLostOnce = false;

        try {
            if (mConnectionReceiver != null) {
                unregisterReceiver(mConnectionReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        registerReceiver(mConnectionReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    @Override
    protected void onStop() {
        try {
            if (mConnectionReceiver != null) {
                unregisterReceiver(mConnectionReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onStop();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);
        if (view instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];
            if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom())) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }

    protected Toast showMessage(String message, int toastDuration) {
        return Toast.makeText(this, message, toastDuration);
    }

    protected <T extends View> T findView(@IdRes int id) {
        return findViewById(id);
    }
}
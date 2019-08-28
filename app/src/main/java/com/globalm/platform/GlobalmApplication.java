package com.globalm.platform;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.net.ConnectivityManager;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.globalm.platform.managers.impl.FileStorageManager;
import com.mapbox.mapboxsdk.Mapbox;

import io.fabric.sdk.android.Fabric;

import static com.globalm.platform.utils.Settings.loadSettingsHelper;

public class GlobalmApplication extends Application {

    private static GlobalmApplication sGlobalmApplication;
    public NotificationManager mNotificationManager;
    private ConnectivityManager mConnectivityManager;

    public static GlobalmApplication getInstance() {
        return sGlobalmApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //disables crashlytics for debug builds
        CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
        Fabric.with(this, new Crashlytics.Builder().core(core).build());
        sGlobalmApplication = this;
        Mapbox.getInstance(getApplicationContext(), getString(R.string.mapbox_access_token));
        loadSettingsHelper(this);
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        FileStorageManager.getInstance(this).checkCreateSyncFolder();
    }

    public boolean isNetworkAvailable() {
        return mConnectivityManager.getActiveNetworkInfo() != null;
    }
}

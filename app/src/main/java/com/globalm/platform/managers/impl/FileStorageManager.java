package com.globalm.platform.managers.impl;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class FileStorageManager {
    private static final String SYNC_FOLDER_NAME = "GlobalmSync";
    private static FileStorageManager instance;
    private Context context;

    public static synchronized FileStorageManager getInstance(Context context) {
        if (instance == null) {
            instance = new FileStorageManager(context);
        }
        return instance;
    }

    private FileStorageManager(Context context) {
        this.context = context;
    }

    public void checkCreateSyncFolder() {
        File file = new File(Environment.getExternalStorageDirectory(), SYNC_FOLDER_NAME);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

}

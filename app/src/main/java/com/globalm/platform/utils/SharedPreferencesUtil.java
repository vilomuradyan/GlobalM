package com.globalm.platform.utils;

public abstract class SharedPreferencesUtil {

    public static void setSyncFolderEnabledOption(boolean syncEnabled) {
        Settings.saveBoolean(Settings.CLOUD_SYNC_ENABLED_KEY, syncEnabled);
    }

    public static boolean getSyncFolderEnabledOption() {
        return Settings.loadBoolean(Settings.CLOUD_SYNC_ENABLED_KEY);
    }

    public static void setUserId(int userId) {
        Settings.saveInt(Settings.USER_ID_KEY, userId);
    }

    public static int getUserId() {
        return Settings.loadInt(Settings.USER_ID_KEY);
    }
}

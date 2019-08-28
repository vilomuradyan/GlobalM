package com.globalm.platform.utils;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Objects;

public class Settings {

    private static SharedPreferences sPreferences;

    public static final String CLOUD_SYNC_ENABLED_KEY = "cloud_sync";
    public static final String USER_ID_KEY = "USER_ID_KEY";


    public static void loadSettingsHelper(Context context) {
        sPreferences = context.getSharedPreferences("com.globalm.platform", Context.MODE_PRIVATE);
    }

    public static void saveString(String key, String value) {
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String loadString(String key) {
        return sPreferences.getString(key, "");
    }

    public static void saveBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean loadBoolean(String key) {
        return sPreferences.getBoolean(key, false);
    }

    public static void saveDouble(String key, double value) {
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putString(key, String.valueOf(value));
        editor.apply();
    }

    public static double loadDouble(String key) {
        return Double.parseDouble(Objects.requireNonNull(sPreferences.getString(key, "0")));
    }

    public static void saveFloat(String key, float value) {
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public static float loadFloat(String key) {
        return sPreferences.getFloat(key, 0);
    }

    public static void saveInt(String key, int value) {
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int loadInt(String key) {
        return sPreferences.getInt(key, 0);
    }
    public static void saveIntCameraSizeWidth(String key, int value) {
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int loadIntCameraSizeWidth(String key) {
        return sPreferences.getInt(key, 1280);
    }

    public static void saveIntCameraSizeHeight(String key, int value) {
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int loadIntCameraSizeHeight(String key) {
        return sPreferences.getInt(key, 720);
    }

    public static void saveStringCameraSize(String key, String value) {
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String loadStringCameraSize(String key) {
        return sPreferences.getString(key, "1280");
    }
}
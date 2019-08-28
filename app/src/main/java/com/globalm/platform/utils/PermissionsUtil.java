package com.globalm.platform.utils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.globalm.platform.R;

import pub.devrel.easypermissions.EasyPermissions;

public abstract class PermissionsUtil {
    public static final int STORAGE_PERMISSION_REQUEST_CODE = 5631;

    public static void processStoragePermissionResult(
            int requestCode,
            @NonNull int[] grantResults,
            Utils.InvokeCallback<Boolean> callback) {
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                callback.invoke(true);
            } else {
                callback.invoke(false);
            }
        }
    }

    public static boolean checkAndRequestPermissions(
            AppCompatActivity activity,
            String[] permissions,
            int requestCode) {
        if (EasyPermissions.hasPermissions(activity, permissions)) {
            return true;
        } else {
            requestPermission(activity, permissions, requestCode);
            return false;
        }
    }

    public static boolean checkAndRequestPermissions(
            Fragment fragment,
            String[] permissions,
            int requestCode) {
        if (EasyPermissions.hasPermissions(fragment.getContext(), permissions)) {
            return true;
        } else {
            requestPermission(fragment, permissions, requestCode);
            return false;
        }
    }

    public static void requestPermission(AppCompatActivity activity,
                                         String[] permissions,
                                         int requestCode) {
        EasyPermissions.requestPermissions(
                activity,
                activity.getResources().getString(R.string.permission_rationale),
                requestCode,
                permissions);
    }

    public static void requestPermission(Fragment fragment, String[] permissions, int requestCode) {
        EasyPermissions.requestPermissions(
                fragment,
                fragment.getResources().getString(R.string.permission_rationale),
                requestCode,
                permissions);
    }

    public static String[] getStoragePermissions() {
        return new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
    }
}

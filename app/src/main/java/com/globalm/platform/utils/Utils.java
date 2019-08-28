package com.globalm.platform.utils;

import android.animation.ObjectAnimator;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Pair;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import timber.log.Timber;

public class Utils {
    public static final String SHARED_FILE_TEMP_NAME = "SHARED_TEMP_FILE";

    public static boolean isValidEmail(String target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static String[] getTimeZoneList() {
        String[] availableIDs = TimeZone.getAvailableIDs();
        String[] timeZoneList = new String[availableIDs.length];
        for (int i = 0; i < availableIDs.length; i++) {
            timeZoneList[i] = (displayTimeZone(TimeZone.getTimeZone(availableIDs[i])));
        }

        return timeZoneList;
    }

    private static String displayTimeZone(TimeZone tz) {
        long hours = TimeUnit.MILLISECONDS.toHours(tz.getRawOffset());
        long minutes = TimeUnit.MILLISECONDS.toMinutes(tz.getRawOffset()) - TimeUnit.HOURS.toMinutes(hours);
        minutes = Math.abs(minutes);
        String result;

        if (hours > 0) {
            result = String.format(Locale.getDefault(), "(GMT+%d:%02d) %s", hours, minutes, tz.getID());
        } else {
            result = String.format(Locale.getDefault(), "(GMT%d:%02d) %s", hours, minutes, tz.getID());
        }

        return result;
    }

    public static void copyFileToDir(Uri filePath,
                                     String directoryPath,
                                     Context context,
                                     String fileName,
                                     InvokeCallback<File> listener) {
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        ContentResolver contentResolver;

        try {
            contentResolver = context.getContentResolver();
            inputStream = contentResolver.openInputStream(filePath);

            byte[] fileReader = new byte[4096];
            File file = new File(
                    directoryPath +
                            "/" +
                            String.format(
                                    "%s.%s",
                                    fileName,
                                    getFileType(context, filePath)
                            ));
            outputStream = new FileOutputStream(file.getAbsolutePath());

            while (true) {
                int read = inputStream.read(fileReader);

                if (read == -1) {
                    break;
                }

                outputStream.write(fileReader, 0 , read);
            }

            outputStream.flush();

            listener.invoke(file);
        } catch (Exception e) {
            e.printStackTrace();
            listener.invoke(null);
        } finally {
            try {
                if (null != inputStream) inputStream.close();
                if (null != outputStream) outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
                listener.invoke(null);
            }
        }

    }

    public static void clearFileFromFilesDir(boolean isFileFromFilesDir, File file) {
        if (!isFileFromFilesDir) return;

        file.delete();
    }

    private static String getFileType(Context context, Uri uri) {
        ContentResolver cR = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public static String calculateMD5(File file) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }

        InputStream is;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            return null;
        }

        byte[] buffer = new byte[8192];
        int read;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            // Fill to 32 chars
            output = String.format("%32s", output).replace(' ', '0');
            return output;
        } catch (IOException e) {
            throw new RuntimeException("Unable to process file for MD5", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Timber.e(e);
            }
        }
    }

    public static <T, R>List<T> map(List<R> list, MapCallback<T, R> callback) {
        List<T> mappedList = new ArrayList<>();

        for (R item : list) {
            mappedList.add(callback.map(item));
        }

        return mappedList;
    }

    public static <T>List<T> filter(List<T> list, FilterCallback<T> callback) {
        List<T> filteredList = new ArrayList<>();

        for (T item : list) {
            if (callback.filter(item)) {
                filteredList.add(item);
            }
        }

        return filteredList;
    }

    @Nullable
    public static <T> T findFirst(List<T> list, FilterCallback<T> callback) {

        for (T item : list) {
            if (callback.filter(item)) {
                return item;
            }
        }

        return null;
    }

    public static <T, R> Map<T, R> toMap(List<R> list, MapCallback<T, R> callback) {
        Map<T, R> map = new HashMap<>();

        for (R item : list) {
            map.put(callback.map(item), item);
        }

        return map;
    }

    public static MultipartBody.Part getMultipartBody(String typeMemo, File file, String partName) {
        MediaType type = MediaType.parse(typeMemo);
        RequestBody requestBody = RequestBody.create(type, file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestBody);
    }

    public static String[] getCountyNamesList() {
        List<String> isoCountries = Arrays.asList(Locale.getISOCountries());
        String[] countyNames = Utils.map(isoCountries, (isoCountry) -> {
            Locale locale = new Locale("en", isoCountry);
            return locale.getDisplayCountry();
        }).toArray(new String[0]);

        return countyNames;
    }

    public static void rotateArrow(ImageView imageView, boolean expand) {
        ObjectAnimator.ofFloat(imageView, "rotation", expand ? 0 : 180, expand ? 180 : 0)
                .start();
    }

    @FunctionalInterface
    public interface InvokeCallback<T> {
        void invoke(@Nullable T result);
    }

    @FunctionalInterface
    public interface MapCallback<T, R> {
        T map(R item);
    }

    @FunctionalInterface
    public interface FilterCallback<T> {
        boolean filter(T item);
    }
}

package com.globalm.platform.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.globalm.platform.R;

public abstract class NotificationUtil {

    public static void showNotification(Context context, String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "")
                .setSmallIcon(R.drawable.ic_notification_app)
                .setColor(ContextCompat.getColor(context, R.color.color_main_blue))
                .setContentTitle(context.getString(R.string.application_name))
                .setContentText(message)
                .setAutoCancel(true)
                .setChannelId(context.getString(R.string.globalm_notification_channel_id));

        mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mBuilder.setPriority(NotificationManager.IMPORTANCE_HIGH);
        } else {
            mBuilder.setPriority(Notification.PRIORITY_HIGH);
        }
        NotificationManager notificationManager
                = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(999, mBuilder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void createNotificationChannel(Context context) {
        String name = context.getString(R.string.application_name);
        String description = context.getString(R.string.notification_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(
                context.getString(R.string.globalm_notification_channel_id),
                name,
                importance);
        channel.setDescription(description);
        channel.enableVibration(true);

        NotificationManager notificationManager
                = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }
}

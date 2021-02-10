package com.example.android.NotificationManeger;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import com.example.android.R;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by Yasmin Jalal - 2020.
 */

class MyNotificationManager {


    private NotificationManager manager;
    private static final String PRIMARY_CHANNEL = "default";
    private Context mCtx;

    public MyNotificationManager(Context mCtx) {
        this.mCtx = mCtx;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel chan1 = null;
            chan1 = new NotificationChannel(PRIMARY_CHANNEL, "1", NotificationManager.IMPORTANCE_HIGH);
            chan1.setLightColor(Color.RED);
            chan1.setShowBadge(true);
            chan1.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManager().createNotificationChannel(chan1);
        } else {
            manager = getManager();
        }

    }

    /**
     * Get a notification of type 1
     * <p>
     * Provide the builder rather than the notification it's self as useful for making notification
     * changes.
     *
     * @param title the title of the notification
     * @param body  the body text for the notification
     * @return the builder as it keeps a reference to the notification (since API 24)
     */
    public Notification.Builder getNotification1(String title, String body, PendingIntent intent) throws ParseException {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return new Notification.Builder(mCtx.getApplicationContext(), PRIMARY_CHANNEL)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setSmallIcon(getSmallIcon())
                    .setShowWhen(true)
                    .setWhen(new Date().getTime())
                    .setContentIntent(intent)
                    .setAutoCancel(true);

        }
        return new Notification.Builder(mCtx.getApplicationContext())
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(intent)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(getSmallIcon())
                .setWhen(new Date().getTime())
                .setAutoCancel(true);

    }

    /**
     * Send a notification.
     *
     * @param id           The ID of the notification
     * @param notification The notification object
     */
    public void notify(int id, Notification.Builder notification) {
        getManager().notify(id, notification.build());
    }

    /**
     * Get the small icon for this app
     *
     * @return The small icon resource id
     */
    private int getSmallIcon() {
        return R.mipmap.ic_launcher;
    }

    /**
     * Get the notification manager.
     * <p>
     * Utility method as this helper works with it a lot.
     *
     * @return The system service NotificationManager
     */
    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

}

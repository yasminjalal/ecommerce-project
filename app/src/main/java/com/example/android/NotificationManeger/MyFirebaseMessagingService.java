package com.example.android.NotificationManeger;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.example.android.Category.MainCategoryActivity;
import com.example.android.SessionManager;

/**
 * Created by Yasmin Jalal - 2020.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private SessionManager session;
    private static final String TAG = "MyFirebaseMsgService";
    private static final String PREFERENCE_LAST_NOTIF_ID = "PREFERENCE_LAST_NOTIF_ID";

    private LocalBroadcastManager broadcaster;

    @Override
    public void onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        session = new SessionManager(this);
        if (session.isLoggedIn()) {
            if (remoteMessage.getData().size() > 0) {
               // Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
                try {
//                    JSONObject json = new JSONObject(remoteMessage.getNotification().toString());
                    //   sendPushNotification(json);
                    sendPushNotification(remoteMessage);
                } catch (Exception e) {
                    Log.e(TAG, "Exception: " + e.getMessage());
                }
            }
        } else {

        }
    }

    //this method will display the notification

    private void sendPushNotification(RemoteMessage remoteMessage) {
        //optionally we can display the json into log
//        Log.e(TAG, "Notification JSON " + remoteMessage.getNotification().getBody());
        try {
            int Id = getNextNotifId(getApplicationContext());
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();


            //creating MyNotificationManager object
            MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());
            Notification.Builder nb = null;
            //creating an intent for the notification
            Intent intent = new Intent(getApplicationContext(), MainCategoryActivity.class);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            getApplicationContext(),
                            Id,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            nb = mNotificationManager.getNotification1(title, body, resultPendingIntent);
            if (nb != null) {
                mNotificationManager.notify(Id, nb);
                Intent broadcastIntent = new Intent("UpdateNotificationBadge");
                broadcaster.sendBroadcast(broadcastIntent);
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    private static int getNextNotifId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int id = sharedPreferences.getInt(PREFERENCE_LAST_NOTIF_ID, 0) + 1;
        if (id == Integer.MAX_VALUE) {
            id = 0;
        }
        sharedPreferences.edit().putInt(PREFERENCE_LAST_NOTIF_ID, id).apply();
        return id;
    }
}

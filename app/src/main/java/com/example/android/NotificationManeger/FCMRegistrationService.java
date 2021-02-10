package com.example.android.NotificationManeger;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.example.android.AppConfig;
import com.example.android.SessionManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yasmin Jalal - 2020.
 */

public class FCMRegistrationService extends IntentService {
    private RequestQueue mRequestQueue;
    private SessionManager session;

    public FCMRegistrationService() {
        super("FCM");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Session manager
        session = new SessionManager(getApplicationContext());
        // get token from Firebase
        String token = FirebaseInstanceId.getInstance().getToken();
        // check if intent is null or not if it isn't null we will ger refreshed value and
        // if its true we will override token_sent value to false and apply
//        if (intent.getExtras() != null) {
//            boolean refreshed = intent.getExtras().getBoolean("refreshed");
//            if (refreshed) preferences.edit().putBoolean("token_sent", false).apply();
//        }

        // if token_sent value is false then use method sendTokenToServer to send token to server
        if (token != null) {
//            if (!session.getDeviceToken().equals(token)) {
            sendTokenToServer(token);
//            }
        }

    }

    // method use volley to send token to server and stop the service when done or error happened
    private void sendTokenToServer(final String token) {
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_REGISTER_DEVICE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //  JSONObject obj = new JSONObject(response);
                        if (response.equals("your request was successfully submitted")) {
//                                preferences.edit().putBoolean("token_sent", false).apply();
                            Log.e("Registration Service", "" + response);
                            session.saveDeviceToken(token);
                            stopSelf();
                        } else {
//                                preferences.edit().putBoolean("token_sent", false).apply();
                            Log.e("Registration Service", "" + response);
                            stopSelf();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        preferences.edit().putBoolean("token_sent", false).apply();
                        Log.e("Registration Service", "" + error.getMessage());
                        stopSelf();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                   params.put("user_id", session.getUserId()+"");
                   params.put("fcm", token);
                return params;
            }
        };
        mRequestQueue.add(stringRequest);

    }

}

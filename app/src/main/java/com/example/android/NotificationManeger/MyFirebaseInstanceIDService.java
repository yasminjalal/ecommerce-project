package com.example.android.NotificationManeger;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.example.android.AppConfig;
import com.example.android.SessionManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yasmin Jalal - 2020.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    private RequestQueue mRequestQueue;
    private SessionManager session;

    @Override
    public void onTokenRefresh() {
        //         Session manager
        session = new SessionManager(getApplicationContext());
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        storeToken(refreshedToken);
    }

    private void storeToken(String token) {
        //saving the token on shared preferences
        //    SharedPreference.getInstance(getApplicationContext()).saveDeviceToken(token);
        session.saveDeviceToken(token);
        sendTokenToServer(token);
    }

    // method use volley to send token to server and stop the service when done or error happened
    private void sendTokenToServer(final String token) {
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_REGISTER_DEVICE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("your request was successfully submitted")) {

                        } else {

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

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

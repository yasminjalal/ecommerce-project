package com.example.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Yasmin Jalal - 2020.
 */

public class SessionManager {
    // Shared preferences file name
    private static final String PREF_NAME = "ecommerceProject";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "USERID";
    private static final String KEY_USER_PHONE_NUMBER = "USERPHONENUMBER";
    private static final String KEY_USER_EMAIL = "USEREMAIL";
    private static final String KEY_USER_FIRST_NAME = "USERFIRSTNAME";
    private static final String KEY_USER_LAST_NAME = "USERLASTNAME";
    private static final String TAG_TOKEN = "tagtoken";
    private static final String TAG_IS_ACTIVE = "ISACTIVE";
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();
    // Shared Preferences
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;
    // Shared pref mode
    private int PRIVATE_MODE = 0;

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.apply();
    }

    public void setLogin(boolean isLoggedIn, int userID, String userPhoneNumber, String userEmail, String userFirstName, String userLastName) {

        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);

        editor.putInt(KEY_USER_ID, userID);

        editor.putString(KEY_USER_PHONE_NUMBER, userPhoneNumber);

        editor.putString(KEY_USER_EMAIL, userEmail);

        editor.putString(KEY_USER_FIRST_NAME, userFirstName);

        editor.putString(KEY_USER_LAST_NAME, userLastName);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void setKeyUserFirstName(String userFirstName) {
        editor.putString(KEY_USER_LAST_NAME, userFirstName);
        // commit changes
        editor.commit();
    }

    public int getUserId() {
        return pref.getInt(KEY_USER_ID, 0);
    }

    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, "empty");
    }

    public void setUserEmail(String userEmail) {
        editor.putString(KEY_USER_EMAIL, userEmail);
        // commit changes
        editor.commit();
    }

    public String getUserPhoneNumber() {
        return pref.getString(KEY_USER_PHONE_NUMBER, "empty");
    }

    public String getUserFirstName() {
        return pref.getString(KEY_USER_FIRST_NAME, "empty");
    }

    public String getUserLastName() {
        return pref.getString(KEY_USER_LAST_NAME, "empty");
    }

    public void setUserLastName(String userLastName) {
        editor.putString(KEY_USER_LAST_NAME, userLastName);
        // commit changes
        editor.commit();
    }

    //this method will save the device token to shared preferences
    public boolean saveDeviceToken(String token) {
        editor.putString(TAG_TOKEN, token);
        // commit changes
        editor.commit();
        return true;
    }

    //this method will fetch the device token from shared preferences
    public String getDeviceToken() {
        return pref.getString(TAG_TOKEN, "empty");
    }

    public void setIsActive(boolean isActive) {
        editor.putBoolean(TAG_IS_ACTIVE, isActive);
        // commit changes
        editor.commit();
    }

    public boolean isActive() {
        return pref.getBoolean(TAG_IS_ACTIVE, false);
    }

}

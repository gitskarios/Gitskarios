package com.alorma.github.sdk.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Bernat on 08/07/2014.
 */
public class StoreCredentials {

    private static final String USER_KEY = StoreCredentials.class.getSimpleName() + ".USER_KEY";
    private static final String USER_PASS = StoreCredentials.class.getSimpleName() + ".USER_PASS";
    private static final String USER_TOKEN = StoreCredentials.class.getSimpleName() + ".USER_TOKEN";
    private final SharedPreferences.Editor editor;
    private final SharedPreferences preferences;

    public StoreCredentials(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
    }

    public void storeToken(String token) {
        editor.putString(USER_TOKEN, token);
        editor.apply();
    }

    public String restoreToken() {
        return preferences.getString(USER_TOKEN, null);
    }
}

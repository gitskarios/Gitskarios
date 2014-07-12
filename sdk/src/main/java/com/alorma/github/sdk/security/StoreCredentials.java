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
    private final SharedPreferences.Editor editor;
    private final SharedPreferences preferences;

    public StoreCredentials(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
    }

    public void storeUser(String user) {
        editor.putString(USER_KEY, user);
        editor.apply();
    }
    public void storePass(String pass) {
        editor.putString(USER_PASS, pass);
        editor.apply();
    }

    public String restoreUser() {
        return preferences.getString(USER_KEY, null);
    }
    public String restorePass() {
        return preferences.getString(USER_PASS, null);
    }
}

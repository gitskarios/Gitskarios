package com.alorma.github;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Bernat on 08/07/2014.
 */
public class StoreCredentials {

    public static final String KEY_URL = "KEY_URL";
    private static final String USER_NAME = StoreCredentials.class.getSimpleName() + ".USER_NAME";
    private static final String USER_TOKEN = StoreCredentials.class.getSimpleName() + ".USER_TOKEN";
    private static final String USER_SCOPES = StoreCredentials.class.getSimpleName() + ".USER_SCOPES";
    private static final String USER_SCOPES_NO_ASK = StoreCredentials.class.getSimpleName() + ".USER_SCOPES_NO_ASK";
    private final SharedPreferences.Editor editor;
    private final SharedPreferences preferences;

    public StoreCredentials(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        editor = preferences.edit();
    }

    public void storeToken(String accessToken) {
        editor.putString(USER_TOKEN, accessToken);
        editor.apply();
    }

    public String token() {
        return preferences.getString(USER_TOKEN, null);
    }

    public void storeScopes(String scopes) {
        editor.putString(USER_SCOPES, scopes);
        editor.commit();
    }

    public String scopes() {
        return preferences.getString(USER_SCOPES, null);
    }

    public void saveScopeNoAsk(boolean scopesNoAsk) {
        editor.putBoolean(USER_SCOPES_NO_ASK, scopesNoAsk);
        editor.commit();
    }

    public Boolean scopeNoAsk() {
        return preferences.getBoolean(USER_SCOPES_NO_ASK, false);
    }

    public void clear() {
        editor.remove(KEY_URL);
        editor.remove(USER_NAME);
        editor.remove(USER_TOKEN);
        editor.remove(USER_SCOPES);
        editor.remove(USER_SCOPES_NO_ASK);
        editor.commit();
    }

    public void storeUsername(String name) {
        editor.putString(USER_NAME, name);
        editor.apply();
    }

    public void storeUrl(String url) {
        editor.putString(KEY_URL, url);
        editor.apply();
    }

    public String getUserName() {
        return preferences.getString(USER_NAME, null);
    }

    public String getUrl() {
        return preferences.getString(KEY_URL, null);
    }
}

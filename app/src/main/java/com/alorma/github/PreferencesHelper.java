package com.alorma.github;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Bernat on 26/09/2014.
 */
public class PreferencesHelper {
    private static final String FILE_NAME = "gitskarios.settings";
    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;

    public PreferencesHelper(Context context) {
        preferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    protected void saveStringSetting(String key, String value) {
        editor.putString(key, value).apply();
    }

    protected String getStringSetting(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }

    protected void saveIntSetting(String key, int value) {
        editor.putInt(key, value).apply();
    }

    protected int getIntSetting(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    protected void saveBooleanSetting(String key, boolean value) {
        editor.putBoolean(key, value).apply();
    }

    protected boolean getBooleanSetting(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    public void registerListener(SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener) {
        preferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    public void unregisterListener(SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener) {
        preferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }
}

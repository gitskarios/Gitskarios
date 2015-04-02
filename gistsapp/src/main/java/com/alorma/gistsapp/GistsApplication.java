package com.alorma.gistsapp;

import android.app.Application;

import com.alorma.github.sdk.security.ApiConstants;
import com.crashlytics.android.Crashlytics;

import net.danlew.android.joda.JodaTimeAndroid;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Bernat on 08/07/2014.
 */
public class GistsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }

        JodaTimeAndroid.init(this);

        ApiConstants.CLIENT_ID = BuildConfig.CLIENT_ID;
        ApiConstants.CLIENT_SECRET = BuildConfig.CLIENT_SECRET;
        ApiConstants.CLIENT_CALLBACK = BuildConfig.CLIENT_CALLBACK;

    }

}

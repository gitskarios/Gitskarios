package com.alorma.github;

import android.app.Application;
import android.os.Trace;

import com.alorma.github.sdk.security.ApiConstants;
import com.alorma.github.sdk.security.StoreCredentials;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by Bernat on 08/07/2014.
 */
public class GistsApplication extends Application{

    private GoogleAnalytics analytics;
    private Tracker tracker;

    @Override
    public void onCreate() {
        super.onCreate();

        ApiConstants.CLIENT_ID = getString(R.string.client_id);
        ApiConstants.CLIENT_SECRET = getString(R.string.client_secret);
        ApiConstants.CLIENT_CALLBACK = getString(R.string.client_callback);

        analytics = GoogleAnalytics.getInstance(this);
    }

    public Tracker getTracker() {
        if (tracker == null) {
            tracker = analytics.newTracker(R.xml.global_tracker);
        }

        return tracker;
    }
}

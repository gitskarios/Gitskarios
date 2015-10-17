package com.alorma.github;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.alorma.github.sdk.security.GithubDeveloperCredentials;
import com.alorma.github.ui.utils.UniversalImageLoaderUtils;
import com.alorma.gitskarios.core.client.credentials.SimpleDeveloperCredentialsProvider;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.danlew.android.joda.JodaTimeAndroid;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Bernat on 08/07/2014.
 */
public class GitskariosApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker tracker = analytics.newTracker(R.xml.global_tracker);
            tracker.enableAdvertisingIdCollection(true);
        }

        GithubDeveloperCredentials.init(new SimpleDeveloperCredentialsProvider(BuildConfig.CLIENT_ID, BuildConfig.CLIENT_SECRET, BuildConfig.CLIENT_CALLBACK));

        JodaTimeAndroid.init(this);

        ImageLoader.getInstance().init(UniversalImageLoaderUtils.getImageLoaderConfiguration(this));
    }

    public static GitskariosApplication get(Context context) {
        return (GitskariosApplication) context.getApplicationContext();
    }

}

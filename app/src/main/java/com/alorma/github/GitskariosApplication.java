package com.alorma.github;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.alorma.github.ui.activity.MainActivity;
import com.alorma.github.ui.utils.UniversalImageLoaderUtils;
import com.alorma.gitskarios.core.client.LogProvider;
import com.alorma.gitskarios.core.client.LogProviderInterface;
import com.alorma.gitskarios.core.client.TokenProvider;
import com.alorma.gitskarios.core.client.TokenProviderInterface;
import com.alorma.gitskarios.core.client.UrlProvider;
import com.alorma.gitskarios.core.client.UrlProviderInterface;
import com.alorma.gitskarios.core.client.UsernameProvider;
import com.alorma.gitskarios.core.client.UsernameProviderInterface;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.ImageLoader;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import io.fabric.sdk.android.Fabric;

public class GitskariosApplication extends MultiDexApplication {

    public static GitskariosApplication get(Context context) {
        return (GitskariosApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        CustomActivityOnCrash.install(this);
        CustomActivityOnCrash.setRestartActivityClass(MainActivity.class);
        CustomActivityOnCrash.setEnableAppRestart(true);

        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker tracker = analytics.newTracker(R.xml.global_tracker);
            tracker.enableAdvertisingIdCollection(true);
        }

        ImageLoader.getInstance().init(UniversalImageLoaderUtils.getImageLoaderConfiguration(this));

        TokenProvider.setTokenProviderInstance(new TokenProviderInterface() {
            @Override
            public String getToken() {
                return getStoreCredentials().token();
            }
        });

        UrlProvider.setUrlProviderInstance(new UrlProviderInterface() {
            @Override
            public String getUrl() {
                return getStoreCredentials().getUrl();
            }
        });

        UsernameProvider.setUsernameProviderInterface(new UsernameProviderInterface() {
            @Override
            public String getUsername() {
                return getStoreCredentials().getUserName();
            }
        });

        LogProvider.setTokenProviderInstance(new LogProviderInterface() {
            @Override
            public void log(String message) {
                if (BuildConfig.DEBUG) {
                    Log.v("RETROFIT_LOG", message);
                }
            }
        });
    }

    @NonNull
    private StoreCredentials getStoreCredentials() {
        return new StoreCredentials(GitskariosApplication.this);
    }
}

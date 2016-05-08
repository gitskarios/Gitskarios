package com.alorma.github;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import com.alorma.github.gcm.GitskariosRegistrationService;
import com.alorma.github.ui.activity.MainActivity;
import com.alorma.github.ui.utils.UniversalImageLoaderUtils;
import com.alorma.gitskarios.core.client.LogProvider;
import com.alorma.gitskarios.core.client.TokenProvider;
import com.alorma.gitskarios.core.client.UrlProvider;
import com.alorma.gitskarios.core.client.UsernameProvider;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.Tracker;
import com.karumi.dexter.Dexter;
import com.nostra13.universalimageloader.core.ImageLoader;
import io.fabric.sdk.android.Fabric;

public class GitskariosApplication extends MultiDexApplication {

  public static GitskariosApplication get(Context context) {
    return (GitskariosApplication) context.getApplicationContext();
  }

  @Override
  public void onCreate() {
    super.onCreate();

    Dexter.initialize(this);

    if (!BuildConfig.DEBUG) {
      CustomActivityOnCrash.install(this);
      CustomActivityOnCrash.setRestartActivityClass(MainActivity.class);
      CustomActivityOnCrash.setEnableAppRestart(true);
    }

    if (!BuildConfig.DEBUG) {
      Fabric.with(this, new Crashlytics());
    }
    AnalyticsTrackers.initialize(this);
    Tracker tracker = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);

    tracker.enableAutoActivityTracking(true);
    tracker.enableExceptionReporting(true);

    ImageLoader.getInstance().init(UniversalImageLoaderUtils.getImageLoaderConfiguration(this));

    TokenProvider.setTokenProviderInstance(() -> getStoreCredentials().token());

    UrlProvider.setUrlProviderInstance(() -> getStoreCredentials().getUrl());

    UsernameProvider.setUsernameProviderInterface(() -> getStoreCredentials().getUserName());

    LogProvider.setTokenProviderInstance(message -> {
      if (BuildConfig.DEBUG) {
        Log.v("RETROFIT_LOG", message);
      }
    });

    if (new GitskariosSettings(this).getGCMToken() == null) {
      Intent intent = new Intent(this, GitskariosRegistrationService.class);
      startService(intent);
    }
  }

  @NonNull
  private StoreCredentials getStoreCredentials() {
    return new StoreCredentials(GitskariosApplication.this);
  }
}

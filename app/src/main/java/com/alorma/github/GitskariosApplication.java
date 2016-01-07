package com.alorma.github;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.alorma.github.ui.activity.MainActivity;
import com.alorma.github.ui.utils.UniversalImageLoaderUtils;
import com.alorma.gitskarios.core.client.StoreCredentials;
import com.alorma.gitskarios.core.client.TokenProvider;
import com.alorma.gitskarios.core.client.TokenProviderInterface;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.ImageLoader;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import io.fabric.sdk.android.Fabric;
import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by Bernat on 08/07/2014.
 */
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

    JodaTimeAndroid.init(this);

    ImageLoader.getInstance().init(UniversalImageLoaderUtils.getImageLoaderConfiguration(this));

    TokenProvider.setTokenProviderInstance(new TokenProviderInterface() {
      @Override
      public String getToken() {
        return new StoreCredentials(GitskariosApplication.this).token();
      }
    });
  }
}

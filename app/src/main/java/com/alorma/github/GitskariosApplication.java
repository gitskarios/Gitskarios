package com.alorma.github;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import com.alorma.github.ui.utils.UniversalImageLoaderUtils;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.ImageLoader;
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

    if (!BuildConfig.DEBUG) {
      Fabric.with(this, new Crashlytics());

      GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
      Tracker tracker = analytics.newTracker(R.xml.global_tracker);
      tracker.enableAdvertisingIdCollection(true);
    }

    JodaTimeAndroid.init(this);

    ImageLoader.getInstance().init(UniversalImageLoaderUtils.getImageLoaderConfiguration(this));
  }
}

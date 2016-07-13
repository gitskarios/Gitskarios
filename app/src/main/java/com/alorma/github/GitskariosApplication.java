package com.alorma.github;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import com.alorma.github.gcm.GitskariosInstanceIDListenerService;
import com.alorma.github.injector.component.ApplicationComponent;
import com.alorma.github.injector.component.DaggerApplicationComponent;
import com.alorma.github.injector.component.DaggerNotificationsComponent;
import com.alorma.github.injector.component.NotificationsComponent;
import com.alorma.github.injector.module.ApplicationModule;
import com.alorma.github.injector.module.NotificationsModule;
import com.alorma.github.notifications.AppNotificationsManager;
import com.alorma.github.ui.activity.MainActivity;
import com.alorma.github.ui.utils.UniversalImageLoaderUtils;
import com.alorma.gitskarios.core.client.LogProvider;
import com.alorma.gitskarios.core.client.TokenProvider;
import com.alorma.gitskarios.core.client.UrlProvider;
import com.alorma.gitskarios.core.client.UsernameProvider;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.nostra13.universalimageloader.core.ImageLoader;
import io.fabric.sdk.android.Fabric;
import javax.inject.Inject;

public class GitskariosApplication extends MultiDexApplication {

  @Inject AppNotificationsManager notificationsManager;

  private ApplicationComponent component;
  private NotificationsComponent notificationsComponent;

  public static GitskariosApplication get(Context context) {
    return (GitskariosApplication) context.getApplicationContext();
  }

  @Override
  public void onCreate() {
    super.onCreate();

    Dexter.initialize(this);

    if (!FirebaseApp.getApps(this).isEmpty()) {
      FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    if (!BuildConfig.DEBUG) {
      CustomActivityOnCrash.install(this);
      CustomActivityOnCrash.setRestartActivityClass(MainActivity.class);
      CustomActivityOnCrash.setEnableAppRestart(true);
    }

    if (!BuildConfig.DEBUG) {
      Fabric.with(this, new Crashlytics());
    }

    FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);
    firebaseAnalytics.setAnalyticsCollectionEnabled(true);

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
      Intent intent = new Intent(this, GitskariosInstanceIDListenerService.class);
      startService(intent);
    }

    initializeInjector();

    notificationsManager.setNotificationsEnabled(notificationsManager.areNotificationsEnabled());
  }

  private void initializeInjector() {
    component = DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(this)).build();
    notificationsComponent = DaggerNotificationsComponent.builder().notificationsModule(new NotificationsModule(this)).build();
    notificationsComponent.inject(this);
  }

  public ApplicationComponent getComponent() {
    return component;
  }

  @NonNull
  private StoreCredentials getStoreCredentials() {
    return new StoreCredentials(GitskariosApplication.this);
  }

  public void setNotificationsEnabled(boolean isChecked) {
    notificationsManager.setNotificationsEnabled(isChecked);
  }
}

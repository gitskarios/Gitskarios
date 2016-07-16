package com.alorma.github.account;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.alorma.github.GitskariosApplication;
import com.alorma.github.injector.component.ApplicationComponent;
import com.alorma.github.injector.component.DaggerNotificationsComponent;
import com.alorma.github.injector.component.NotificationsComponent;
import com.alorma.github.injector.module.NotificationsModule;
import com.alorma.github.notifications.AppNotificationsManager;
import javax.inject.Inject;

public class StartUpBootReceiver extends BroadcastReceiver {

  @Inject AppNotificationsManager appNotificationsManager;

  @Override
  public void onReceive(Context context, Intent intent) {
    if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
      injectComponents(context);

      if (appNotificationsManager != null) {
        appNotificationsManager.setNotificationsEnabled(appNotificationsManager.areNotificationsEnabled());
      }
    }
  }

  private void injectComponents(Context context) {
    GitskariosApplication application = (GitskariosApplication) context.getApplicationContext();
    ApplicationComponent applicationComponent = application.getApplicationComponent();

    NotificationsComponent notificationsComponent = DaggerNotificationsComponent.builder()
        .applicationComponent(applicationComponent)
        .notificationsModule(new NotificationsModule())
        .build();
    notificationsComponent.inject(this);
  }
}

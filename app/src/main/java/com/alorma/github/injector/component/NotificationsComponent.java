package com.alorma.github.injector.component;

import com.alorma.github.GitskariosApplication;
import com.alorma.github.account.StartUpBootReceiver;
import com.alorma.github.injector.NotificationsScope;
import com.alorma.github.injector.module.NotificationsModule;
import com.alorma.github.notifications.AppJobManager;
import com.alorma.github.notifications.AppNotificationsManager;
import com.alorma.github.ui.activity.NotificationsActivity;
import dagger.Component;
import javax.inject.Named;

@NotificationsScope @Component(modules = NotificationsModule.class, dependencies = ApplicationComponent.class)
public interface NotificationsComponent {

  @Named("AlarmManagerNotificationsJobManager")
  AppJobManager getJobManager();

  AppNotificationsManager getNotificationsManager();

  void inject(GitskariosApplication application);

  void inject(NotificationsActivity activity);

  void inject(StartUpBootReceiver startUpBootReceiver);
}

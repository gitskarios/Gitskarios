package com.alorma.github.injector.module;

import com.alorma.github.injector.NotificationsScope;
import com.alorma.github.notifications.AppJobManager;
import com.alorma.github.notifications.AppNotificationsManager;
import com.alorma.github.notifications.AppNotificationsManagerImpl;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;

@Module public class NotificationsModule {

  @Provides
  @NotificationsScope
  @Named("NotificationsJobManager")
  AppJobManager getJobManager() {
    return new AppJobManager() {
      @Override
      public void enable() {

      }

      @Override
      public void disable() {

      }
    };
  }

  @Provides
  @NotificationsScope
  AppNotificationsManager getNotificationsManager(@Named("NotificationsJobManager") AppJobManager jobManager) {
    return new AppNotificationsManagerImpl(jobManager);
  }
}

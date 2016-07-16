package com.alorma.github.injector.module;

import android.content.Context;
import com.alorma.github.injector.NotificationsScope;
import com.alorma.github.notifications.AlarmManagerJobManager;
import com.alorma.github.notifications.AppJobManager;
import com.alorma.github.notifications.AppNotificationsManager;
import com.alorma.github.notifications.AppNotificationsManagerImpl;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;

@Module public class NotificationsModule {

  @Provides
  @NotificationsScope
  @Named("AlarmManagerNotificationsJobManager")
  AppJobManager getJobManager(Context context) {
    return new AlarmManagerJobManager(context);
  }

  @Provides
  @NotificationsScope
  AppNotificationsManager getNotificationsManager(Context context, @Named("AlarmManagerNotificationsJobManager") AppJobManager jobManager) {
    return new AppNotificationsManagerImpl(context, jobManager);
  }
}

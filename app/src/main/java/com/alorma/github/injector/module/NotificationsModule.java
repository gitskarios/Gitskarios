package com.alorma.github.injector.module;

import android.content.Context;
import com.alorma.github.injector.NotificationsScope;
import com.alorma.github.notifications.AppJobManager;
import com.alorma.github.notifications.AppNotificationsManager;
import com.alorma.github.notifications.AppNotificationsManagerImpl;
import com.alorma.github.notifications.JobDispatcherJobManager;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;

@Module public class NotificationsModule {

  @Provides
  @NotificationsScope
  @Named("NotificationsJobManager")
  AppJobManager getJobManager(Context context) {
    return new JobDispatcherJobManager(context);
  }

  @Provides
  @NotificationsScope
  AppNotificationsManager getNotificationsManager(Context context, @Named("NotificationsJobManager") AppJobManager jobManager) {
    return new AppNotificationsManagerImpl(context, jobManager);
  }
}

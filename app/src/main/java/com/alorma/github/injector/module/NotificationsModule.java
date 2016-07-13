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

  private Context context;

  public NotificationsModule(Context context) {

    this.context = context;
  }

  @Provides
  @NotificationsScope
  @Named("NotificationsJobManager")
  AppJobManager getJobManager() {
    return new JobDispatcherJobManager(context);
  }

  @Provides
  @NotificationsScope
  AppNotificationsManager getNotificationsManager(@Named("NotificationsJobManager") AppJobManager jobManager) {
    return new AppNotificationsManagerImpl(context, jobManager);
  }
}

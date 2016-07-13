package com.alorma.github.notifications;

import android.content.Context;
import com.alorma.github.GitskariosSettings;

public class AppNotificationsManagerImpl implements AppNotificationsManager {
  private final GitskariosSettings gitskariosSettings;
  private AppJobManager jobManager;

  public AppNotificationsManagerImpl(Context context, AppJobManager jobManager) {
    gitskariosSettings = new GitskariosSettings(context);
    this.jobManager = jobManager;
  }

  @Override
  public boolean areNotificationsEnabled() {
    return gitskariosSettings.areNotificationsEnabled();
  }

  @Override
  public void setNotificationsEnabled(boolean enabled) {
    gitskariosSettings.setNotificationsEnabled(enabled);
    if (enabled) {
      jobManager.enable();
    } else {
      jobManager.disable();
    }
  }
}

package com.alorma.github.notifications;

public class AppNotificationsManagerImpl implements AppNotificationsManager {
  private AppJobManager jobManager;

  public AppNotificationsManagerImpl(AppJobManager jobManager) {

    this.jobManager = jobManager;
  }

  @Override
  public boolean areNotificationsEnabled() {
    return true;
  }

  @Override
  public void setNotificationsEnabled(boolean enabled) {
    if (enabled) {
      jobManager.enable();
    } else {
      jobManager.disable();
    }
  }
}

package com.alorma.github.notifications;

public interface AppNotificationsManager {
  boolean areNotificationsEnabled();
  void setNotificationsEnabled(boolean enabled);
}

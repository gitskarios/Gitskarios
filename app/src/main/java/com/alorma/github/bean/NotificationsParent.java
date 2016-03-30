package com.alorma.github.bean;

import com.alorma.github.sdk.core.Repo;
import com.alorma.github.sdk.core.notifications.Notification;
import java.util.List;

public class NotificationsParent {
  public Repo repo;
  public List<Notification> notifications;
}

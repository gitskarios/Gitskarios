package com.alorma.github.bean;

import com.alorma.github.sdk.core.notifications.Notification;
import com.alorma.github.sdk.core.repositories.Repo;
import java.util.List;

public class NotificationsParent {
  public Repo repo;
  public List<Notification> notifications;
}

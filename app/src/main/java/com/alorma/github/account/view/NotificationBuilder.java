package com.alorma.github.account.view;

import android.app.NotificationManager;
import core.notifications.Notification;
import java.util.List;

public interface NotificationBuilder {
  void fire(NotificationManager manager, List<Notification> notifications);
}

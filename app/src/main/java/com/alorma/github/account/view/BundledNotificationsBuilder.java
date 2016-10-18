package com.alorma.github.account.view;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import com.alorma.github.R;
import com.alorma.github.ui.activity.NotificationsActivity;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import core.repositories.Repo;
import java.util.List;

public class BundledNotificationsBuilder implements NotificationBuilder {

  private Context context;
  private final SimpleNotificationBuilder simpleNotificationBuilder;

  public BundledNotificationsBuilder(Context context) {
    this.context = context;
    simpleNotificationBuilder = new SimpleNotificationBuilder(context);
  }

  @Override
  public void fire(NotificationManager manager, List<core.notifications.Notification> notifications) {
    for (core.notifications.Notification notification : notifications) {
      fireSummary(manager, notification.getRepository());
      simpleNotificationBuilder.fireSimple(manager, notification);
    }
  }

  private void fireSummary(NotificationManager manager, Repo repository) {
    String repoName = repository.getFullName();
    long repoId = repository.getId();

    Intent intent = NotificationsActivity.launchIntent(context);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    PendingIntent pendingIntent = PendingIntent.getActivity(context, 11, intent, 0);

    NotificationCompat.Builder summary = new NotificationCompat.Builder(context).setColor(getColor(repoName))
        .setSmallIcon(R.drawable.ic_stat_name)
        .setShowWhen(false)
        .setLocalOnly(true)
        .setContentTitle(repoName)
        .setGroup(repoName)
        .setGroupSummary(true)
        .setOnlyAlertOnce(true)
        .setContentIntent(pendingIntent);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      summary.setCategory(Notification.CATEGORY_EVENT);
    }

    manager.notify((int) repoId, summary.build());
  }

  private int getColor(String repoName) {
    return ColorGenerator.MATERIAL.getColor(repoName);
  }
}

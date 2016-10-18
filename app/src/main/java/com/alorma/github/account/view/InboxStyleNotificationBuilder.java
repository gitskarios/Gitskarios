package com.alorma.github.account.view;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import com.alorma.github.R;
import com.alorma.github.ui.activity.NotificationsActivity;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import core.notifications.Notification;
import core.repositories.Repo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InboxStyleNotificationBuilder implements NotificationBuilder {

  private Context context;
  private static final int MAX_LINES_NOTIFICATION = 5;

  public InboxStyleNotificationBuilder(Context context) {
    this.context = context;
  }

  @Override
  public void fire(NotificationManager manager, List<Notification> notifications) {

    Map<Long, RepositoryBundle> repositoryBundleMap = new HashMap<>();

    for (Notification notification : notifications) {
      Repo repository = notification.getRepository();
      if (repositoryBundleMap.get(repository.getId()) == null) {
        repositoryBundleMap.put(repository.getId(), new RepositoryBundle(repository));
      }
      repositoryBundleMap.get(repository.getId()).add(notification);
    }

    for (Long id : repositoryBundleMap.keySet()) {
      RepositoryBundle repositoryBundle = repositoryBundleMap.get(id);
      fireNotificationByRepository(manager, repositoryBundle.getRepository(), repositoryBundle);
    }
  }

  private void fireNotificationByRepository(NotificationManager manager, Repo repository, List<Notification> notifications) {
    if (notifications != null && notifications.size() > 0) {
      Intent intent = NotificationsActivity.launchIntent(context);

      PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

      NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setColor(getColor(repository.getFullName()))
          .setSmallIcon(R.drawable.ic_stat_name)
          .setShowWhen(false)
          .setLocalOnly(true)
          .setContentTitle(repository.getFullName())
          .setOnlyAlertOnce(true)
          .setContentIntent(pendingIntent);

      builder.setContentText(notifications.size() + " notifications");

      NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

      for (int i = 0; i < Math.min(MAX_LINES_NOTIFICATION, notifications.size()); i++) {
        Notification notification = notifications.get(i);
        StringBuilder stringBuilder = new StringBuilder().append("<b>")
            .append("[")
            .append(notification.subject.type)
            .append("] ")
            .append("</b>")
            .append(notification.subject.title);

        inboxStyle.addLine(Html.fromHtml(stringBuilder.toString()));
      }

      if (notifications.size() > MAX_LINES_NOTIFICATION) {
        inboxStyle.addLine("...");
        inboxStyle.setBigContentTitle(notifications.get(0).repository.getFullName());
        inboxStyle.setSummaryText(" +" + (notifications.size() - MAX_LINES_NOTIFICATION) + " more");
      }

      builder.setStyle(inboxStyle);

      android.app.Notification notification = builder.build();

      manager.notify((int) repository.getId(), notification);
    }
  }

  private int getColor(String repoName) {
    return ColorGenerator.MATERIAL.getColor(repoName);
  }

  private class RepositoryBundle extends ArrayList<Notification> {

    private final Repo repository;

    public RepositoryBundle(Repo repository) {

      this.repository = repository;
    }

    public Repo getRepository() {
      return repository;
    }
  }
}

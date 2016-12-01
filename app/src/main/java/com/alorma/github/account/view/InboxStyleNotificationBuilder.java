package com.alorma.github.account.view;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import com.alorma.github.R;
import com.alorma.github.ui.activity.NotificationsActivity;
import com.alorma.github.utils.AttributesUtils;
import core.notifications.Notification;
import java.util.List;

public class InboxStyleNotificationBuilder implements NotificationBuilder {

  private Context context;
  private static final int MAX_LINES_NOTIFICATION = 5;
  public static final int NOTIFICATIONS_INBOX = 1101;

  public InboxStyleNotificationBuilder(Context context) {
    this.context = context;
  }

  @Override
  public void fire(NotificationManager manager, List<Notification> notifications) {
    fireNotificationByRepository(manager, notifications);
  }

  private void fireNotificationByRepository(NotificationManager manager, List<Notification> notifications) {
    if (notifications != null && notifications.size() > 0) {
      Intent intent = NotificationsActivity.launchIntent(context);

      PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

      int primaryColor = AttributesUtils.getPrimaryColor(context);
      NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setColor(primaryColor)
          .setSmallIcon(R.drawable.ic_stat_name)
          .setShowWhen(false)
          .setLocalOnly(true)
          .setContentTitle(notifications.size() + " Notifications")
          .setOnlyAlertOnce(true)
          .setDefaults(android.app.Notification.DEFAULT_LIGHTS)
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

      manager.notify(NOTIFICATIONS_INBOX, notification);
    }
  }
}

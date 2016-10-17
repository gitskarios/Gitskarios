package com.alorma.github.account.view;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import com.alorma.github.R;
import com.alorma.github.account.NotificationsDisableService;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.activity.IssueDetailActivity;
import com.alorma.github.ui.activity.NotificationsActivity;
import com.alorma.github.ui.activity.PullRequestDetailActivity;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import core.repositories.Repo;
import java.util.List;

public class BundledNotificationsBuilder implements NotificationBuilder {

  private Context context;

  public BundledNotificationsBuilder(Context context) {
    this.context = context;
  }

  @Override
  public void fire(NotificationManager manager, List<core.notifications.Notification> notifications) {
    for (core.notifications.Notification notification : notifications) {
      fireSummary(manager, notification.getRepository());
      fireSimple(manager, notification);
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

  private void fireSimple(NotificationManager manager, core.notifications.Notification notification) {
    String notificationType = notification.getSubject().getType();
    String notificationTitle = notification.getSubject().getTitle();
    String fullName = notification.getRepository().getFullName();

    NotificationCompat.Builder not = new NotificationCompat.Builder(context)
        .setDefaults(Notification.DEFAULT_ALL)
        .setColor(getColor(fullName))
        .setTicker(notificationTitle)
        .setWhen(notification.getUpdated_at().getTime())
        .setSmallIcon(R.drawable.ic_stat_name)
        .setContentTitle(notificationTitle)
        .setContentText(fullName)
        .setSubText(notificationType)
        .setContentIntent(getPendingIntent(notification))
        .setOnlyAlertOnce(true)
        .setLocalOnly(true)
        .setAutoCancel(true)
        .setGroup(fullName)
        .setDeleteIntent(getCancelIntent(notification))
        .setLargeIcon(getAvatar(context, notification));

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      not.setCategory(Notification.CATEGORY_EVENT);
    }

    manager.notify((int) notification.getId(), not.build());
  }

  private int getColor(String repoName) {
    return ColorGenerator.MATERIAL.getColor(repoName);
  }

  private PendingIntent getCancelIntent(core.notifications.Notification notification) {
    Intent intentDisableService = NotificationsDisableService.createIntentSingleNotification(context, notification);
    return PendingIntent.getService(context, (int) notification.getId(), intentDisableService, PendingIntent.FLAG_ONE_SHOT);
  }

  private PendingIntent getPendingIntent(core.notifications.Notification notification) {

    IssueInfo issueInfo = new IssueInfo();
    issueInfo.repoInfo = new RepoInfo();
    issueInfo.repoInfo.owner = notification.getRepository().getOwner().getLogin();
    issueInfo.repoInfo.name = notification.getRepository().getName();
    issueInfo.num = (int) ContentUris.parseId(Uri.parse(notification.getSubject().getUrl()));

    Intent intent = null;
    if ("issue".equalsIgnoreCase(notification.getSubject().getType())) {
      intent = IssueDetailActivity.createLauncherIntent(context, issueInfo);
    } else if ("PullRequest".equalsIgnoreCase(notification.getSubject().getType())) {
      intent = PullRequestDetailActivity.createLauncherIntent(context, issueInfo);
    }

    if (intent != null) {
      return PendingIntent.getActivity(context, 1234, intent, 0);
    }

    return null;
  }

  private Bitmap getAvatar(Context context, core.notifications.Notification model) {
    String avatar = model.getRepository().getOwner().getAvatar();
    if (avatar != null) {
      try {
        int avatar_size = (int) context.getResources().getDimension(R.dimen.notification_size);
        return Glide.with(context)
            .load(avatar)
            .asBitmap()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(R.drawable.ic_stat_name)
            .into(avatar_size, avatar_size)
            .get();
      } catch (Exception e) {
        return getDefaultProfileImage(context);
      }
    }
    return getDefaultProfileImage(context);
  }

  private Bitmap getDefaultProfileImage(Context context) {
    return BitmapFactory.decodeResource(context.getApplicationContext().getResources(), R.drawable.ic_stat_name);
  }
}

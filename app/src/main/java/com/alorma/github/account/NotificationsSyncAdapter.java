package com.alorma.github.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.Html;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Notification;
import com.alorma.github.sdk.services.notifications.GetNotificationsClient;
import com.alorma.github.ui.activity.NotificationsActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Bernat on 07/06/2015.
 */
public class NotificationsSyncAdapter extends AbstractThreadedSyncAdapter {

    public NotificationsSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public NotificationsSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        String token = AccountManager.get(getContext()).getUserData(account, AccountManager.KEY_AUTHTOKEN);

        if (token != null) {
            NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(token.hashCode());

            GetNotificationsClient notificationsClient = new GetNotificationsClient(getContext(), token);
            List<Notification> notifications = notificationsClient.executeSync();

            if (notifications != null) {
                if (notifications.size() == 1) {
                    fireSingleNotifications(account.name, token, notifications.get(0));
                } else if (notifications.size() > 0) {
                    Map<Long, List<Notification>> notificationMap = new HashMap<>();

                    for (Notification notification : notifications) {
                        if (notification.repository != null) {
                            if (notificationMap.get(notification.repository.id) == null) {
                                notificationMap.put(notification.repository.id, new ArrayList<Notification>());
                            }
                            notificationMap.get(notification.repository.id).add(notification);
                        }
                    }

/*                    if (notificationMap.size() == 1) {
                        Long repoId = notificationMap.keySet().iterator().next();
                        List<Notification> notificationList = notificationMap.get(repoId);
                        if (notificationList != null) {
                            if (notificationList.size() == 1) {
                                fireSingleNotifications(account.name, token, notificationList.get(0));
                            } else if (notificationList.size() <= 3) {
                                for (Notification notification : notificationList) {
                                    fireSingleNotifications(account.name, token, notification);
                                }
                            } else {
                                fireNotificationByRepository(account.name, token, repoId, notificationList);
                            }
                        }
                    } else*/

                    if (notificationMap.size() <= 3) {
                        for (Long repoId : notificationMap.keySet()) {
                            List<Notification> notificationList = notificationMap.get(repoId);
                            if (notificationList != null) {
                                if (notificationList.size() == 1) {
                                    fireSingleNotifications(account.name, token, notificationList.get(0));
                                } else {
                                    fireNotificationByRepository(account.name, token, repoId, notificationList);
                                }
                            }
                        }
                    } else {
                        fireAllInOneNotifications(account.name, token, notifications, notificationMap.size());
                    }
                }
            }
        }
    }

    private NotificationCompat.Builder createNotificationBuilder(PendingIntent pendingIntent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext());
        builder.setSmallIcon(R.drawable.ic_stat_name);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setLights(getContext().getResources().getColor(R.color.primary), 1000, 2000);
        return builder;
    }

    @NonNull
    private StringBuilder getExpandedText(List<Notification> notifications) {
        StringBuilder msgBuilder = new StringBuilder();

        for (int i = 0; i < notifications.size(); i++) {
            Notification notification = notifications.get(i);
            msgBuilder.append("<b>")
                    .append("[")
                    .append(notification.subject.type)
                    .append("] ")
                    .append("</b>")
                    .append(notification.subject.title);

            if (i < notifications.size() - 1) {
                msgBuilder.append("<br />");
            }
        }
        return msgBuilder;
    }

    private void fireSingleNotifications(String account, String token, Notification githubNotification) {
        if (githubNotification != null) {
            Intent intent = NotificationsActivity.launchIntent(getContext(), token);

            PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);

            NotificationCompat.Builder builder = createNotificationBuilder(pendingIntent);

            builder.setContentTitle(account + ": " + githubNotification.repository.full_name);

            StringBuilder msgBuilder = new StringBuilder();
            msgBuilder.append("<b>")
                    .append("[")
                    .append(githubNotification.subject.type)
                    .append("] ")
                    .append("</b>")
                    .append(githubNotification.subject.title);

            builder.setContentText(Html.fromHtml(msgBuilder.toString()));
            builder.setSubText(account);

            android.app.Notification notification = builder.build();

            fireNotification((int) githubNotification.id, notification);
        }
    }

    private void fireNotificationByRepository(String account, String token, long repoId, List<Notification> notifications) {
        if (notifications != null && notifications.size() > 0) {
            Intent intent = NotificationsActivity.launchIntent(getContext(), token);

            PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);

            NotificationCompat.Builder builder = createNotificationBuilder(pendingIntent);

            builder.setContentTitle(getContext().getResources().getString(R.string.app_name));
            builder.setContentText(notifications.get(0).repository.full_name + " " + notifications.size() + " notifications");
            builder.setSubText(account);

            StringBuilder msgBuilder = getExpandedText(notifications);

            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(msgBuilder.toString())));

            android.app.Notification notification = builder.build();

            fireNotification((int) repoId, notification);
        }
    }

    private void fireAllInOneNotifications(String account, String token, List<Notification> notifications, int reposSize) {
        if (notifications != null && notifications.size() > 0) {
            Intent intent = NotificationsActivity.launchIntent(getContext(), token);

            PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);

            NotificationCompat.Builder builder = createNotificationBuilder(pendingIntent);

            builder.setContentTitle(getContext().getResources().getString(R.string.app_name));
            builder.setContentText(notifications.size() + " notifications in " + reposSize + " repositories");
            builder.setSubText(account);

            StringBuilder msgBuilder = getExpandedText(notifications);

            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(msgBuilder.toString()));

            android.app.Notification notification = builder.build();

            fireNotification(token.hashCode(), notification);
        }
    }

    private void fireNotification(int notificationId, android.app.Notification notification) {
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationId, notification);
    }
}
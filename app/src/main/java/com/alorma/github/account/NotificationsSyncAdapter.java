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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.Html;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Notification;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.services.notifications.GetNotificationsClient;
import com.alorma.github.ui.activity.NotificationsActivity;
import com.alorma.github.utils.AttributesUtils;
import com.alorma.github.utils.NotificationsHelper;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Bernat on 07/06/2015.
 */
public class NotificationsSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final int MAX_LINES_NOTIFICATION = 5;

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
            final NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(token.hashCode());

            GetNotificationsClient notificationsClient = new GetNotificationsClient(token);
            notificationsClient.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new NotificationsSubscriber(account, token));
        }
    }

    private void onNotificationsReceived(List<Notification> notifications, Account account, String token) {
        if (notifications != null) {
            List<Notification> newNotifications = new ArrayList<>();

            for (Notification notification : notifications) {
                boolean showNotification = NotificationsHelper.checkNotFireNotification(getContext(), notification.id);
                if (showNotification) {
                    newNotifications.add(notification);
                }
            }
            notifications = newNotifications;

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
            }
        }
    }

    private NotificationCompat.Builder createNotificationBuilder(Repo repository, String account, PendingIntent pendingIntent,
                                                                 @Nullable PendingIntent cancelIntent) {

        int color = AttributesUtils.getPrimaryColor(getContext());
        int shape_notifications_avatar = getContext().getResources().getDimensionPixelOffset(R.dimen.shape_notifications_avatar);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext());
        builder.setContentTitle(repository.full_name);
        builder.setSmallIcon(R.drawable.ic_stat_name);
        builder.setSubText(account);

        try {
            String owner = String.valueOf(repository.owner.login.charAt(0));
            String repo = String.valueOf(repository.name.charAt(0));

            String key = owner + repo;

            ColorGenerator generator = ColorGenerator.MATERIAL;

            TextDrawable drawable = TextDrawable.builder()
                    .beginConfig()
                    .width(shape_notifications_avatar)
                    .height(shape_notifications_avatar)
                    .endConfig()
                    .buildRound(key, generator.getColor(key));

            Bitmap bitmap = null;
            if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            builder.setLargeIcon(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        builder.setContentIntent(pendingIntent);
        if (cancelIntent != null) {
            builder.setDeleteIntent(cancelIntent);
        }
        builder.setAutoCancel(true);
        builder.setColor(color);
        builder.setLights(color, 1000, 2000);
        return builder;
    }

    private void fireSingleNotifications(String account, String token, Notification githubNotification) {
        if (githubNotification != null) {
            Intent intentNotificationsActivity = NotificationsActivity.launchIntent(getContext(), token);
            PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intentNotificationsActivity, 0);

            Intent intentDisableService = NotificationsDisableService.createIntentSingleNotification(getContext(), githubNotification);
            PendingIntent cancelIntent =
                    PendingIntent.getService(getContext(), (int) githubNotification.id, intentDisableService, PendingIntent.FLAG_ONE_SHOT);

            NotificationCompat.Builder builder = createNotificationBuilder(githubNotification.repository, account, pendingIntent, cancelIntent);

            StringBuilder msgBuilder = new StringBuilder();
            msgBuilder.append("<b>")
                    .append("[")
                    .append(githubNotification.subject.type)
                    .append("] ")
                    .append("</b>")
                    .append(githubNotification.subject.title);

            builder.setContentText(Html.fromHtml(msgBuilder.toString()));

            android.app.Notification notification = builder.build();

            fireNotification((int) githubNotification.id, notification);
        }
    }

    private void fireNotificationByRepository(String account, String token, long repoId, List<Notification> notifications) {
        if (notifications != null && notifications.size() > 0) {
            Intent intent = NotificationsActivity.launchIntent(getContext(), token);

            PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);

            NotificationCompat.Builder builder = createNotificationBuilder(notifications.get(0).repository, account, pendingIntent, null);

            builder.setContentText(notifications.size() + " notifications");

            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

            for (int i = 0; i < Math.min(MAX_LINES_NOTIFICATION, notifications.size()); i++) {
                Notification notification = notifications.get(i);
                StringBuilder msgBuilder = new StringBuilder();
                msgBuilder.append("<b>")
                        .append("[")
                        .append(notification.subject.type)
                        .append("] ")
                        .append("</b>")
                        .append(notification.subject.title);

                inboxStyle.addLine(Html.fromHtml(msgBuilder.toString()));
            }

            if (notifications.size() > MAX_LINES_NOTIFICATION) {
                inboxStyle.addLine("...");
                inboxStyle.setBigContentTitle(notifications.get(0).repository.full_name);
                inboxStyle.setSummaryText(" +" + (notifications.size() - MAX_LINES_NOTIFICATION) + " more");
            }

            builder.setStyle(inboxStyle);

            android.app.Notification notification = builder.build();

            fireNotification((int) repoId, notification);
        }
    }

    private void fireNotification(int notificationId, android.app.Notification notification) {
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationId, notification);
    }

    private class NotificationsSubscriber extends Subscriber<List<Notification>> {

        private Account account;
        private String token;

        public NotificationsSubscriber(Account account, String token) {

            this.account = account;
            this.token = token;
        }

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(List<Notification> notifications) {
            onNotificationsReceived(notifications, account, token);
        }
    }
}
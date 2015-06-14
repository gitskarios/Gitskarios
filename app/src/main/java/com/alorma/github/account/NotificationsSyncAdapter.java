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
import android.support.v4.app.NotificationCompat;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Notification;
import com.alorma.github.sdk.services.notifications.GetNotificationsClient;
import com.alorma.github.ui.activity.NotificationsActivity;

import java.util.List;

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
            GetNotificationsClient notificationsClient = new GetNotificationsClient(getContext(), token);
            List<Notification> notifications = notificationsClient.executeSync();

            if (notifications != null && notifications.size() > 0) {
                Intent intent = NotificationsActivity.launchIntent(getContext(), token);

                PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);


                NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext());
                builder.setContentTitle(getContext().getResources().getString(R.string.app_name));
                builder.setContentText(account.name + " " + notifications.size() + " notifications");
                builder.setSmallIcon(R.drawable.ic_stat_name);
                builder.setContentIntent(pendingIntent);
                builder.setAutoCancel(true);
                builder.setLights(getContext().getResources().getColor(R.color.primary), 1000, 2000);

                android.app.Notification notification = builder.build();

                NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.notify(token.hashCode(), notification);
            }
        }
    }
}

package com.alorma.github.account;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import com.alorma.github.utils.NotificationsHelper;
import core.notifications.Notification;

public class NotificationsDisableService extends Service {

  public static final String EXTRA_NOTIFICATION_ID = "EXTRA_NOTIFICATION_ID";
  public static final String EXTRA_NOTIFICATION_IDS = "EXTRA_NOTIFICATION_IDS";

  public static Intent createIntentSingleNotification(Context context, Notification notification) {
    Intent intent = new Intent(context, NotificationsDisableService.class);

    intent.putExtra(EXTRA_NOTIFICATION_ID, notification.id);

    return intent;
  }

  /*
   * Instantiate the sync adapter object.
   */
  @Override
  public void onCreate() {

  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {

    if (intent.getExtras() != null) {
      if (intent.getExtras().containsKey(EXTRA_NOTIFICATION_ID)) {
        long notificationId = intent.getLongExtra(EXTRA_NOTIFICATION_ID, -1);
        if (notificationId != -1) {
          NotificationsHelper.addNotFireNotification(this, notificationId);
        }
      }
    }

    return START_NOT_STICKY;
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}

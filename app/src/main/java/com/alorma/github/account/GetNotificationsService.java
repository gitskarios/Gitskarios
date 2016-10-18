package com.alorma.github.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.alorma.github.AccountsHelper;
import com.alorma.github.R;
import com.alorma.github.account.view.BundledNotificationsBuilder;
import com.alorma.github.account.view.InboxStyleNotificationBuilder;
import com.alorma.github.account.view.NotificationBuilder;
import com.alorma.github.account.view.SimpleNotificationBuilder;
import com.alorma.github.sdk.services.notifications.GetNotificationsClient;
import com.alorma.github.utils.AccountUtils;
import com.alorma.github.utils.NotificationsHelper;
import core.notifications.Notification;
import java.util.ArrayList;
import java.util.List;
import rx.Subscription;
import rx.schedulers.Schedulers;

public class GetNotificationsService extends Service {

  public static final String ACCOUNT_NAME = "ACCOUNT_NAME";
  public static final String ACCOUNT_TOKEN = "ACCOUNT_TOKEN";

  private static final int MAX_LINES_NOTIFICATION = 5;
  private Subscription subscription;

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {

    if (intent.getExtras() != null && intent.getExtras().containsKey(ACCOUNT_NAME) && intent.getExtras().containsKey(ACCOUNT_TOKEN)) {
      String name = intent.getExtras().getString(ACCOUNT_NAME);
      String token = intent.getExtras().getString(ACCOUNT_TOKEN);
      checkNotifications(name, token);
    } else {
      AccountManager accountManager = AccountManager.get(this);

      Account[] accounts = accountManager.getAccountsByType(getString(R.string.account_type));

      AccountUtils accountUtils = new AccountUtils();
      for (Account account : accounts) {
        String name = accountUtils.getNameFromAccount(account.name);
        String token = AccountsHelper.getUserToken(this, account);
        checkNotifications(name, token);
      }
    }

    return Service.START_NOT_STICKY;
  }

  private void checkNotifications(String name, String token) {
    if (name != null && token != null) {
      final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
      notificationManager.cancel(token.hashCode());

      GetNotificationsClient notificationsClient = new GetNotificationsClient(token);
      subscription = notificationsClient.observable()
          .subscribeOn(Schedulers.io())
          .observeOn(Schedulers.io())
          .subscribe(this::onNotificationsReceived, Throwable::printStackTrace);
    }
  }

  @Override
  public void onDestroy() {
    if (subscription != null) {
      subscription.unsubscribe();
    }
    super.onDestroy();
  }

  private void onNotificationsReceived(List<Notification> notifications) {

    if (notifications != null) {
      List<Notification> newNotifications = new ArrayList<>();

      for (Notification notification : notifications) {
        boolean showNotification = NotificationsHelper.checkNotFireNotification(this, notification.id);
        //if (showNotification) {
          newNotifications.add(notification);
        //}
      }
      notifications = newNotifications;

      NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
      if (notifications.size() > 1) {
        NotificationBuilder builder = new InboxStyleNotificationBuilder(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
          builder = new BundledNotificationsBuilder(this);
        }

        builder.fire(manager, notifications);
      } else if (notifications.size() == 1) {
        new SimpleNotificationBuilder(this).fireSimple(manager, notifications.get(0));
      }
    }
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}

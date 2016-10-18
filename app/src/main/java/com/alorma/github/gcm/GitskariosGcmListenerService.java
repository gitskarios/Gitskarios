package com.alorma.github.gcm;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import com.alorma.github.R;
import com.alorma.github.account.GetNotificationsService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import core.User;
import core.issues.Issue;
import core.repositories.Repo;

public class GitskariosGcmListenerService extends FirebaseMessagingService {

  private static final String TAG = "MyGcmListenerService";

  /**
   * Called when message is received.
   *
   * For Set of keys use data.keySet().
   */
  // [START receive_message]
  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
    super.onMessageReceived(remoteMessage);

    if (remoteMessage.getFrom().startsWith("/topics/")) {

      defaultNotifications();

      /*
      if (remoteMessage.getData() != null) {
        String action = remoteMessage.getData().get("action");
        if (action != null) {
          Gson gson = new Gson();
          Repo repo = gson.fromJson(remoteMessage.getData().get("repository"), Repo.class);
          Issue issue = gson.fromJson(remoteMessage.getData().get("issue"), Issue.class);
          User sender = gson.fromJson(remoteMessage.getData().get("sender"), User.class);
          summaryNotification(repo);
          if (action.equals("opened")) {
            openIssueNotification(repo, issue, sender);
          } else if (action.equals("closed")) {
            closeIssueNotification(repo, issue, sender);
          }
        } else {
          defaultNotifications();
        }
      }
      */
    }
  }

  private void summaryNotification(Repo repo) {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
    builder.setContentTitle(repo.getFullName());
    builder.setSmallIcon(R.mipmap.ic_launcher);
    builder.setColor(ContextCompat.getColor(this, R.color.primary)).setGroup(repo.getFullName()).setGroupSummary(true);

    NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    nm.notify((int) repo.getId(), builder.build());
  }

  private void openIssueNotification(Repo repo, Issue issue, User sender) {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
    builder.setContentTitle("Issue opened");
    builder.setContentText(sender.getLogin() + " opened issue " + issue.getTitle() + " on " + repo.getFullName());
    builder.setSmallIcon(R.mipmap.ic_launcher);
    builder.setColor(ContextCompat.getColor(this, R.color.primary)).setGroup(repo.getFullName());

    NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    nm.notify((int) issue.getId(), builder.build());
  }

  private void closeIssueNotification(Repo repo, Issue issue, User sender) {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
    builder.setContentTitle("Issue closed");
    builder.setContentText(sender.getLogin() + " closed issue " + issue.getTitle() + " on " + repo.getFullName());
    builder.setSmallIcon(R.mipmap.ic_launcher);
    builder.setColor(ContextCompat.getColor(this, R.color.primary)).setGroup(repo.getFullName());

    NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    nm.notify((int) issue.getId(), builder.build());
  }

  private void defaultNotifications() {
    Account[] accounts = AccountManager.get(this).getAccountsByType(getString(R.string.account_type));

    for (Account account : accounts) {

      String name = account.name;

      String token = AccountManager.get(this).getUserData(account, AccountManager.KEY_AUTHTOKEN);

      Intent intent = new Intent(this, GetNotificationsService.class);
      intent.putExtra(GetNotificationsService.ACCOUNT_NAME, name);
      intent.putExtra(GetNotificationsService.ACCOUNT_TOKEN, token);
      this.startService(intent);
    }
  }
}

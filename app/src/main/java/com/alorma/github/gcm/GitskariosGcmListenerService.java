package com.alorma.github.gcm;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.util.Log;
import com.alorma.github.R;
import com.alorma.github.account.GetNotificationsService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

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

    String message = remoteMessage.getNotification().getBody();
    Log.d(TAG, "From: " + remoteMessage.getFrom());
    Log.d(TAG, "Message: " + message);

    if (remoteMessage.getFrom().startsWith("/topics/")) {
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
}

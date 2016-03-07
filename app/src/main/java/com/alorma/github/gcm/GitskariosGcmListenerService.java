package com.alorma.github.gcm;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.alorma.github.R;
import com.alorma.github.account.GetNotificationsService;
import com.google.android.gms.gcm.GcmListenerService;

public class GitskariosGcmListenerService extends GcmListenerService {

  private static final String TAG = "MyGcmListenerService";

  /**
   * Called when message is received.
   *
   * @param from SenderID of the sender.
   * @param data Data bundle containing message data as key/value pairs.
   * For Set of keys use data.keySet().
   */
  // [START receive_message]
  @Override
  public void onMessageReceived(String from, Bundle data) {
    String message = data.getString("message");
    Log.d(TAG, "From: " + from);
    Log.d(TAG, "Message: " + message);

    if (from.startsWith("/topics/")) {
      Account[] accounts =
          AccountManager.get(this).getAccountsByType(getString(R.string.account_type));

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

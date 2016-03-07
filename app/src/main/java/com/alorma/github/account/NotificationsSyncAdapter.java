package com.alorma.github.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;

/**
 * Created by Bernat on 07/06/2015.
 */
public class NotificationsSyncAdapter extends AbstractThreadedSyncAdapter {

  public NotificationsSyncAdapter(Context context, boolean autoInitialize) {
    super(context, autoInitialize);
  }

  public NotificationsSyncAdapter(Context context, boolean autoInitialize,
      boolean allowParallelSyncs) {
    super(context, autoInitialize, allowParallelSyncs);
  }

  @Override
  public void onPerformSync(Account account, Bundle extras, String authority,
      ContentProviderClient provider, SyncResult syncResult) {

    String name = account.name;

    String token =
        AccountManager.get(getContext()).getUserData(account, AccountManager.KEY_AUTHTOKEN);

    Intent intent = new Intent(getContext(), GetNotificationsService.class);
    intent.putExtra(GetNotificationsService.ACCOUNT_NAME, name);
    intent.putExtra(GetNotificationsService.ACCOUNT_TOKEN, token);
    getContext().startService(intent);
  }
}
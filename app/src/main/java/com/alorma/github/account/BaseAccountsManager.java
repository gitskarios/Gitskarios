package com.alorma.github.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import com.alorma.github.R;
import com.alorma.github.ui.actions.Action;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bernat.borras on 24/10/15.
 */
public abstract class BaseAccountsManager {

  @NonNull
  public List<Account> getAccounts(Context context) {

    AccountManager accountManager = AccountManager.get(context);

    List<Account> accountList = new ArrayList<>();

    for (String accountType : getAccountTypes(context)) {
      Account[] accounts = accountManager.getAccountsByType(accountType);
      accountList.addAll(Arrays.asList(accounts));
    }
    return accountList;
  }

  protected abstract String[] getAccountTypes(Context context);

  public void removeAccount(Activity activity, Account selectedAccount, final RemoveAccountCallback removeAccountCallback) {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
        AccountManagerCallback<Bundle> callback = new AccountManagerCallback<Bundle>() {
          @Override
          public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
            if (accountManagerFuture.isDone()) {

              if (removeAccountCallback != null) {
                removeAccountCallback.onAccountRemoved();
              }
            }
          }
        };
        AccountManager.get(activity).removeAccount(selectedAccount, activity, callback, new Handler());
      } else {
        AccountManagerCallback<Boolean> callback = new AccountManagerCallback<Boolean>() {
          @Override
          public void run(AccountManagerFuture<Boolean> accountManagerFuture) {
            if (accountManagerFuture.isDone()) {

              if (removeAccountCallback != null) {
                removeAccountCallback.onAccountRemoved();
              }
            }
          }
        };
        AccountManager.get(activity).removeAccount(selectedAccount, callback, new Handler());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void changeNotificationState(Context context, Account account, boolean enabled) {
    if (account != null) {
      if (enabled) {
        ContentResolver.addPeriodicSync(account, account.type, Bundle.EMPTY, 1800);
        ContentResolver.setSyncAutomatically(account, account.type, true);
      } else {
        ContentResolver.removePeriodicSync(account, context.getString(R.string.account_type), Bundle.EMPTY);
        ContentResolver.setSyncAutomatically(account, account.type, false);
      }
    }
  }

  public abstract boolean multipleAccountsAllowed();

  public interface RemoveAccountCallback {
    void onAccountRemoved();
  }
}

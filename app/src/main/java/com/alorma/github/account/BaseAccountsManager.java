package com.alorma.github.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BaseAccountsManager {

  @NonNull
  @SuppressWarnings({"MissingPermission"})
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

  public void removeAccount(Activity activity, Account selectedAccount,
      final RemoveAccountCallback removeAccountCallback) {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
        AccountManagerCallback<Bundle> callback = accountManagerFuture -> {
          if (accountManagerFuture.isDone()) {

            if (removeAccountCallback != null) {
              removeAccountCallback.onAccountRemoved();
            }
          }
        };
        AccountManager.get(activity)
            .removeAccount(selectedAccount, activity, callback, new Handler());
      } else {
        AccountManagerCallback<Boolean> callback = accountManagerFuture -> {
          if (accountManagerFuture.isDone()) {

            if (removeAccountCallback != null) {
              removeAccountCallback.onAccountRemoved();
            }
          }
        };
        AccountManager.get(activity).removeAccount(selectedAccount, callback, new Handler());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public abstract boolean multipleAccountsAllowed();

  public interface RemoveAccountCallback {
    void onAccountRemoved();
  }
}

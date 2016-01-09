package com.alorma.github.account;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

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
        if (account != null && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_SYNC_SETTINGS)
                == PackageManager.PERMISSION_GRANTED) {
            String authority = account.type.replace(".account", "");
            if (enabled) {
                ContentResolver.setIsSyncable(account, authority, 1);
                ContentResolver.addPeriodicSync(account, authority, Bundle.EMPTY, 1800);
            } else {
                ContentResolver.removePeriodicSync(account, authority, Bundle.EMPTY);
            }
            ContentResolver.setSyncAutomatically(account, authority, enabled);
        }
    }

    public abstract boolean multipleAccountsAllowed();

    public interface RemoveAccountCallback {
        void onAccountRemoved();
    }
}

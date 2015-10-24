package com.alorma.github.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Fragment;
import android.content.ContentResolver;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import com.alorma.github.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bernat.borras on 24/10/15.
 */
public abstract class BaseAccountsFragmentManager extends Fragment {

    @NonNull
    public List<Account> getAccounts() {

        AccountManager accountManager = AccountManager.get(getActivity());

        List<Account> accountList = new ArrayList<>();

        String[] accountTypes = new String[]{getString(R.string.account_type)};

            for (String accountType : getAccountTypes()) {
                Account[] accounts = accountManager.getAccountsByType(accountType);
                accountList.addAll(Arrays.asList(accounts));
            }
        return accountList;
    }

    protected abstract String[] getAccountTypes();

    public void removeAccount(Account selectedAccount, final RemoveAccountCallback removeAccountCallback) {
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
                AccountManager.get(getActivity()).removeAccount(selectedAccount, getActivity(), callback, new Handler());
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
                AccountManager.get(getActivity()).removeAccount(selectedAccount, callback, new Handler());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeNotificationState(Account account, boolean enabled) {
        if (account != null) {
            if (enabled) {
                ContentResolver.addPeriodicSync(account, account.type, Bundle.EMPTY, 1800);
                ContentResolver.setSyncAutomatically(account, account.type, true);
            } else {
                ContentResolver.removePeriodicSync(
                    account,
                    getString(R.string.account_type),
                    Bundle.EMPTY
                );
                ContentResolver.setSyncAutomatically(account, account.type, false);
            }
        }
    }

    public interface RemoveAccountCallback {
        void onAccountRemoved();
    }

    public abstract boolean multipleAccountsAllowed();
}

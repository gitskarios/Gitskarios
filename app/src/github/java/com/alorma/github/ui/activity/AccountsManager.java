package com.alorma.github.ui.activity;

import android.content.Context;

import com.alorma.github.BuildConfig;
import com.alorma.github.R;
import com.alorma.github.account.BaseAccountsManager;

/**
 * Created by bernat.borras on 24/10/15.
 */
public class AccountsManager extends BaseAccountsManager {
    @Override
    protected String[] getAccountTypes(Context context) {
        return new String[]{context.getString(R.string.account_type)};
    }

    @Override
    public boolean multipleAccountsAllowed() {
        return BuildConfig.DEBUG;
    }
}

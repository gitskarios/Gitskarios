package com.alorma.github.ui.activity;

import com.alorma.github.R;
import com.alorma.github.account.BaseAccountsFragmentManager;

/**
 * Created by bernat.borras on 24/10/15.
 */
public class AccountsFragmentManager extends BaseAccountsFragmentManager {
    @Override
    protected String[] getAccountTypes() {
        return new String[]{getString(R.string.account_type)};
    }
}

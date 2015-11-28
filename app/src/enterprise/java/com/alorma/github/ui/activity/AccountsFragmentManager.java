package com.alorma.github.ui.activity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import com.alorma.github.R;
import com.alorma.github.account.BaseAccountsManager;

/**
 * Created by bernat.borras on 24/10/15.
 */
public class AccountsFragmentManager extends BaseAccountsManager {

  private static final String KEY_IMPORT = "KEY_IMPORT";

  @Override
  protected String[] getAccountTypes() {
    Boolean importAccounts = getImportAccounts();
    if (importAccounts != null && importAccounts) {
      return new String[] { getString(R.string.account_type), getString(R.string.enterprise_account_type) };
    } else {
      return new String[] { getString(R.string.enterprise_account_type) };
    }
  }

  @Nullable
  private Boolean getImportAccounts() {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

    Boolean importAccounts = null;
    if (preferences.contains(KEY_IMPORT)) {
      importAccounts = preferences.getBoolean(KEY_IMPORT, false);
    }
    return importAccounts;
  }

  @Override
  public boolean multipleAccountsAllowed() {
    return true;
  }
}

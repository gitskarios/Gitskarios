package com.alorma.github.ui.activity.base;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;
import com.alorma.github.R;
import com.alorma.github.sdk.login.AccountsHelper;
import com.alorma.github.ui.activity.AccountsFragmentManager;
import com.alorma.github.ui.activity.MainActivity;
import com.alorma.gitskarios.core.client.StoreCredentials;
import com.alorma.gitskarios.core.client.UnAuthIntent;
import dmax.dialog.SpotsDialog;
import java.util.List;

/**
 * Created by Bernat on 19/07/2014.
 */
public class BaseActivity extends AppCompatActivity {

  public static final String EXTRA_WITH_TOKEN = "ETXRA_WOTH_TOKEN";

  private AuthReceiver authReceiver;
  private UpdateReceiver updateReceiver;

  private Toolbar toolbar;
  private SpotsDialog progressDialog;
  private AccountsFragmentManager accountsFragmentManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    accountsFragmentManager = new AccountsFragmentManager();
    getFragmentManager().beginTransaction().add(accountsFragmentManager, "accountsFragmentManager").commit();
  }

  @Override
  public void setContentView(int layoutResID) {
    super.setContentView(layoutResID);
    if (isToolbarEnabled()) {
      toolbar = (Toolbar) findViewById(getToolbarId());

      if (toolbar != null) {
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
      }
    }
  }

  public boolean isToolbarEnabled() {
    return true;
  }

  public Toolbar getToolbar() {
    return toolbar;
  }

  public int getToolbarId() {
    return R.id.toolbar;
  }

  @Override
  protected void onResume() {
    super.onResume();
    LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
    authReceiver = new AuthReceiver();
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(UnAuthIntent.ACTION);
    manager.registerReceiver(authReceiver, intentFilter);
  }

  @Override
  public void setTitle(CharSequence title) {
    if (toolbar != null) {
      toolbar.setTitle(title);
    } else {
      super.setTitle(title);
    }
  }

  @Override
  public void setTitle(int titleId) {
    if (toolbar != null) {
      toolbar.setTitle(titleId);
    }
    super.setTitle(titleId);
  }

  @Override
  protected void onPause() {
    super.onPause();
    LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
    manager.unregisterReceiver(authReceiver);
  }

  @NonNull
  protected List<Account> getAccounts() {
    return accountsFragmentManager.getAccounts();
  }

  protected void removeAccount(Account selectedAccount, final AccountsFragmentManager.RemoveAccountCallback removeAccountCallback) {
    accountsFragmentManager.removeAccount(selectedAccount, removeAccountCallback);
  }

  protected void changeNotificationState(Account account, boolean enabled) {
    accountsFragmentManager.changeNotificationState(account, enabled);
  }

  public void reload() {
    getContent();
  }

  protected void getContent() {

  }

  @Override
  public void onStart() {
    super.onStart();
    updateReceiver = new UpdateReceiver();
    IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    registerReceiver(updateReceiver, intentFilter);
  }

  @Override
  public void onStop() {
    super.onStop();
    unregisterReceiver(updateReceiver);
  }

  protected void showProgressDialog(@StyleRes int style) {
    if (progressDialog == null) {
      try {
        progressDialog = new SpotsDialog(this, style);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  protected void hideProgressDialog() {
    if (progressDialog != null) {
      progressDialog.dismiss();
      progressDialog = null;
    }
  }

  private class AuthReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
      String token = intent.getStringExtra(UnAuthIntent.TOKEN);

      AccountManager accountManager = AccountManager.get(context);
      Account[] accounts = accountManager.getAccountsByType(getString(R.string.account_type));

      for (final Account account : accounts) {
        if (AccountsHelper.getUserToken(context, account).equals(token)) {
          try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
              AccountManagerCallback<Bundle> callback = new AccountManagerCallback<Bundle>() {
                @Override
                public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
                  if (accountManagerFuture.isDone()) {

                    StoreCredentials storeCredentials = new StoreCredentials(BaseActivity.this);
                    storeCredentials.clear();

                    Toast.makeText(BaseActivity.this, getString(R.string.unauthorized, account.name), Toast.LENGTH_SHORT).show();
                    Intent loginIntent = new Intent(BaseActivity.this, MainActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                    finish();
                  }
                }
              };
              accountManager.removeAccount(account, BaseActivity.this, callback, new Handler());
            } else {
              AccountManagerCallback<Boolean> callback = new AccountManagerCallback<Boolean>() {
                @Override
                public void run(AccountManagerFuture<Boolean> accountManagerFuture) {
                  if (accountManagerFuture.isDone()) {

                    StoreCredentials storeCredentials = new StoreCredentials(BaseActivity.this);
                    storeCredentials.clear();

                    Toast.makeText(BaseActivity.this, getString(R.string.unauthorized, account.name), Toast.LENGTH_SHORT).show();
                    Intent loginIntent = new Intent(BaseActivity.this, MainActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                    finish();
                  }
                }
              };
              accountManager.removeAccount(account, callback, new Handler());
            }
          } catch (Exception e) {
            e.printStackTrace();
          }

          break;
        }
      }
    }
  }

  public class UpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

      if (isOnline(context)) {
        reload();
      }
    }

    public boolean isOnline(Context context) {
      ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo netInfoMob = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
      NetworkInfo netInfoWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
      return (netInfoMob != null && netInfoMob.isConnectedOrConnecting()) || (netInfoWifi != null && netInfoWifi.isConnectedOrConnecting());
    }
  }
}

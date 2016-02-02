package com.alorma.github.ui.activity.base;

import android.accounts.Account;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.alorma.github.R;
import com.alorma.github.ui.activity.AccountsManager;

import java.util.List;

/**
 * Created by Bernat on 19/07/2014.
 */
public class BaseActivity extends AppCompatActivity {

    public static final String EXTRA_WITH_TOKEN = "EXTRA_WITH_TOKEN";

    private Toolbar toolbar;
    private AccountsManager accountsManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        configureTheme();
        super.onCreate(savedInstanceState);
        accountsManager = new AccountsManager();
    }

    protected void configureTheme() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String pref_theme = defaultSharedPreferences.getString("pref_theme", getString(R.string.theme_light));
        if ("theme_dark".equalsIgnoreCase(pref_theme)) {
            setTheme(R.style.AppTheme_Dark);
        }
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

    @NonNull
    protected List<Account> getAccounts() {
        return accountsManager.getAccounts(this);
    }

    protected void removeAccount(Account selectedAccount, final AccountsManager.RemoveAccountCallback removeAccountCallback) {
        accountsManager.removeAccount(this, selectedAccount, removeAccountCallback);
    }

    protected void changeNotificationState(Account account, boolean enabled) {
        accountsManager.changeNotificationState(this, account, enabled);
    }

    public void reload() {
        getContent();
    }

    protected void getContent() {

    }

    protected void showProgressDialog(@StringRes int text) {
        if (progressDialog == null) {
            try {
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage(getString(text));
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
}

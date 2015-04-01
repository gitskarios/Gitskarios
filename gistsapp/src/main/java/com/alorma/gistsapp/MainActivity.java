package com.alorma.gistsapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Account[] accounts = AccountManager.get(this).getAccountsByType(getString(R.string.account_type));

        Toast.makeText(this, "Accounts: " + accounts.length, Toast.LENGTH_SHORT).show();
    }
}

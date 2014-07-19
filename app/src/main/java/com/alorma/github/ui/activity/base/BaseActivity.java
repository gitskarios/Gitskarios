package com.alorma.github.ui.activity.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.alorma.github.R;
import com.alorma.github.sdk.security.UnAuthIntent;
import com.alorma.github.ui.activity.LoginActivity;

/**
 * Created by Bernat on 19/07/2014.
 */
public class BaseActivity extends Activity {

    private AuthReceiver authReceiver;

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
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.unregisterReceiver(authReceiver);
    }

    private class AuthReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, R.string.unauthorized, Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(context, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }
}

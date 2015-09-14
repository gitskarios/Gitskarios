package com.alorma.github.ui.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;

import com.alorma.github.R;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.NotificationsFragment;

/**
 * Created by a557114 on 23/04/2015.
 */
public class NotificationsActivity extends BackActivity {

    public static Intent launchIntent(Context context) {
        return new Intent(context, NotificationsActivity.class);
    }

    public static Intent launchIntent(Context context, String token) {
        Intent intent = launchIntent(context);
        intent.putExtra(EXTRA_WITH_TOKEN, token);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.generic_toolbar);

        NotificationsFragment notificationsFragment = NotificationsFragment.newInstance();
        notificationsFragment.setArguments(getIntent().getExtras());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content, notificationsFragment);
        ft.commit();

        if (getToolbar() != null) {
            ViewCompat.setElevation(getToolbar(), 4);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
}

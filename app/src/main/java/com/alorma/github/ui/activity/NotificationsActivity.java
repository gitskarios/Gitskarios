package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.generic_toolbar);

        NotificationsFragment notificationsFragment = NotificationsFragment.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content, notificationsFragment);
        ft.commit();
    }
}

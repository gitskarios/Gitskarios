package com.alorma.github.ui.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.events.ColorEvent;
import com.alorma.github.ui.fragment.ProfileFragment;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * Created by Bernat on 15/07/2014.
 */
public class ProfileActivity extends BackActivity {

    private Bus bus;

    public static Intent createLauncherIntent(Context context) {
        Intent intent = new Intent(context, ProfileActivity.class);
        return intent;
    }

    public static Intent createLauncherIntent(Context context, User user) {
        Intent intent = new Intent(context, ProfileActivity.class);
        if (user != null) {
            intent.putExtra(ProfileFragment.USER, user);
        }
        return intent;
    }

    public static Intent createIntentFilterLauncherActivity(Context context, User user) {
        Intent intent = new Intent(context, ProfileActivity.class);
        if (user != null) {
            intent.putExtra(ProfileFragment.USER, user);
            intent.putExtra(ProfileFragment.FROM_INTENT_FILTER, true);
        }
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bus = new Bus();
        bus.register(this);

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey(ProfileFragment.USER)) {
                User user = getIntent().getParcelableExtra(ProfileFragment.USER);
                boolean fromIntentFilter = getIntent().getBooleanExtra(ProfileFragment.FROM_INTENT_FILTER, false);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(android.R.id.content, ProfileFragment.newInstance(user, fromIntentFilter));
                ft.commit();
            } else {
                finish();
            }
        } else {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(android.R.id.content, ProfileFragment.newInstance());
            ft.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Subscribe
    public void colorReceived(ColorEvent event) {
        setUpActionBarColor(event.getRgb());
    }
}

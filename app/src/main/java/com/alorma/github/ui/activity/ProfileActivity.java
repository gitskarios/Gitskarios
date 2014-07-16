package com.alorma.github.ui.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.ui.fragment.ProfileFragment;

/**
 * Created by Bernat on 15/07/2014.
 */
public class ProfileActivity extends Activity {

    public static Intent createLauncherIntent(Context context, String username) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra(ProfileFragment.USERNAME, username);
        return intent;
    }

    public static Intent createLauncherIntent(Context context, User user) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra(ProfileFragment.USER, user);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey(ProfileFragment.USER)) {
                User user = getIntent().getParcelableExtra(ProfileFragment.USER);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(android.R.id.content, ProfileFragment.newInstance(user));
                ft.commit();
            } else if (getIntent().getExtras().containsKey(ProfileFragment.USERNAME)) {
                String username = getIntent().getStringExtra(ProfileFragment.USERNAME);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(android.R.id.content, ProfileFragment.newInstance(username));
                ft.commit();
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

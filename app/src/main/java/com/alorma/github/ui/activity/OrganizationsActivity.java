package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.alorma.github.R;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.orgs.OrganizationsFragment;

/**
 * Created by Bernat on 31/03/2015.
 */
public class OrganizationsActivity extends BackActivity {
    private static final String USER = "USER";

    public static Intent launchIntent(Context context, String user) {
        Intent intent = new Intent(context, OrganizationsActivity.class);
        intent.putExtra(USER, user);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_toolbar);

        String user = getIntent().getStringExtra(USER);

        setTitle(getString(R.string.title_activity_orgs, user));

        OrganizationsFragment orgsFragment = OrganizationsFragment.newInstance(user);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content, orgsFragment);
        ft.commit();
    }
}

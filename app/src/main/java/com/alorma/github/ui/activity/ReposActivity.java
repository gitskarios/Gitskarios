package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.UserType;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.orgs.OrgsReposFragment;
import com.alorma.github.ui.fragment.repos.UsernameReposFragment;

public class ReposActivity extends BackActivity {

    private static final String USER = "USER";
    private static final String USER_TYPE = "USER_TYPE";

    public static Intent launchIntent(Context context, String user, UserType type) {
        Intent intent = new Intent(context, ReposActivity.class);
        intent.putExtra(USER, user);
        intent.putExtra(USER_TYPE, type.toString());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_toolbar);

        String user = getIntent().getStringExtra(USER);
        String userType = getIntent().getStringExtra(USER_TYPE);

        setTitle(getString(R.string.title_activity_repos, user));

        if (userType.equalsIgnoreCase("user")) {
            UsernameReposFragment currentAccountReposFragment = UsernameReposFragment.newInstance(user);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content, currentAccountReposFragment);
            ft.commit();
        } else {
            OrgsReposFragment orgsReposFragment = OrgsReposFragment.newInstance(user);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content, orgsReposFragment);
            ft.commit();
        }
    }
}

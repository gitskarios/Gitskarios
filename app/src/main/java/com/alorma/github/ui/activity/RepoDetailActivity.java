package com.alorma.github.ui.activity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.detail.repo.RepoDetailFragment;

/**
 * Created by Bernat on 17/07/2014.
 */
public class RepoDetailActivity extends BackActivity {

    public static Intent createLauncherActivity(Context context, String owner, String repo, String description) {
        Bundle bundle = new Bundle();
        bundle.putString(RepoDetailFragment.OWNER, owner);
        bundle.putString(RepoDetailFragment.REPO, repo);
        bundle.putString(RepoDetailFragment.DESCRIPTION, description);
        bundle.putBoolean(RepoDetailFragment.FROM_INTENT_FILTER, false);

        Intent intent = new Intent(context, RepoDetailActivity.class);
        intent.putExtras(bundle);
        return intent;
    }
    public static Intent createIntentFilterLauncherActivity(Context context, String owner, String repo, String description) {
        Bundle bundle = new Bundle();
        bundle.putString(RepoDetailFragment.OWNER, owner);
        bundle.putString(RepoDetailFragment.REPO, repo);
        bundle.putString(RepoDetailFragment.DESCRIPTION, description);
        bundle.putBoolean(RepoDetailFragment.FROM_INTENT_FILTER, true);

        Intent intent = new Intent(context, RepoDetailActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() != null) {
            String owner = getIntent().getExtras().getString(RepoDetailFragment.OWNER);
            String repo = getIntent().getExtras().getString(RepoDetailFragment.REPO);
            String description = getIntent().getExtras().getString(RepoDetailFragment.DESCRIPTION);
            boolean from = getIntent().getExtras().getBoolean(RepoDetailFragment.FROM_INTENT_FILTER);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(android.R.id.content, RepoDetailFragment.newInstance(owner, repo, description, from));
            ft.commit();
        } else {
            finish();
        }
    }
}

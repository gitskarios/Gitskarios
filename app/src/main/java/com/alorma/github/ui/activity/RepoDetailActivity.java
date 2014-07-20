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

    public static Intent createLauncherActivity(Context context, String owner, String repo) {
        Bundle bundle = new Bundle();
        bundle.putString(RepoDetailFragment.OWNER, owner);
        bundle.putString(RepoDetailFragment.REPO, repo);

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

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(android.R.id.content, RepoDetailFragment.newInstance(owner, repo));
            ft.commit();
        } else {
            finish();
        }
    }
}

package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.PullRequest;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.pullrequest.PullRequestCommitsListFragment;

/**
 * Created by Bernat on 17/06/2015.
 */
public class PullRequestCommitsActivity extends BackActivity {

    public static final String ISSUE_INFO = "ISSUE_INFO";

    public static Intent launchIntent(Context context, PullRequest pullRequest, IssueInfo info) {
        Intent intent = new Intent(context, PullRequestCommitsActivity.class);
        intent.putExtra(ISSUE_INFO, info);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_toolbar);

        if (getIntent() != null && getIntent().getExtras() != null) {
            IssueInfo info = (IssueInfo) getIntent().getParcelableExtra(ISSUE_INFO);

            setTitle(info.toString());

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content, PullRequestCommitsListFragment.newInstance(info));
            ft.commit();
        }
    }
}

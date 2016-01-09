package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.CommitInfo;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.commit.CommitStatusFragment;

/**
 * Created by a557114 on 06/09/2015.
 */
public class StatusActivity extends BackActivity {

    public static final String COMMIT_INFO = "COMMIT_INFO";
    public static final String ISSUE_INFO = "ISSUE_INFO";

    public static Intent launchIntent(Context context, IssueInfo issueInfo, CommitInfo info) {
        Intent intent = new Intent(context, StatusActivity.class);
        intent.putExtra(ISSUE_INFO, issueInfo);
        intent.putExtra(COMMIT_INFO, info);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_toolbar);

        if (getIntent() != null && getIntent().getExtras() != null) {
            IssueInfo issueInfo = (IssueInfo) getIntent().getParcelableExtra(ISSUE_INFO);
            CommitInfo info = (CommitInfo) getIntent().getParcelableExtra(COMMIT_INFO);

            setTitle(getString(R.string.status_screen_title, issueInfo.toString()));

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content, CommitStatusFragment.newInstance(info));
            ft.commit();
        }
    }
}

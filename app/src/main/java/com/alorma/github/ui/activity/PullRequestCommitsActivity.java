package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;

import com.alorma.github.sdk.PullRequest;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.ui.activity.base.BackActivity;

/**
 * Created by Bernat on 17/06/2015.
 */
public class PullRequestCommitsActivity extends BackActivity {


    public static Intent launchIntent(Context context, PullRequest pullRequest, IssueInfo info) {
        return new Intent(context, PullRequestCommitsActivity.class);
    }
}

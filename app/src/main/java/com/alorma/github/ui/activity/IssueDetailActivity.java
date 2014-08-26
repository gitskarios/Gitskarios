package com.alorma.github.ui.activity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FABCenterLayout;

import com.alorma.github.R;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.ActionRepoListener;
import com.alorma.github.ui.fragment.detail.issue.IssueDetailInfoFragment;
import com.alorma.github.ui.fragment.detail.issue.IssueDiscussionFragment;
import com.alorma.github.ui.listeners.RefreshListener;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.dvilleneuve.android.TextDrawable;

public class IssueDetailActivity extends BackActivity implements ActionRepoListener, RefreshListener {

    private static final String NUMBER = "NUMBER";
    public static final String OWNER = "OWNER";
    public static final String REPO = "REPO";
    private String owner;
    private String repo;
    private int number;
    private SmoothProgressBar smoothBar;
    private FABCenterLayout fabLayout;
    private IssueDiscussionFragment issueDiscussionFragment;
    private IssueDetailInfoFragment issueInfoFragment;
    private View headerView;
    private int mActionBarHeight;

    private TypedValue mTypedValue = new TypedValue();
    private float headerViewHeight;
    private View discussionView;
    private float discussionViewHeight;
    private float mHeaderTransformFactor;

    public static Intent createLauncherIntent(Context context, String owner, String repo, int number) {
        Bundle bundle = new Bundle();

        bundle.putInt(NUMBER, number);
        bundle.putString(OWNER, owner);
        bundle.putString(REPO, repo);

        Intent intent = new Intent(context, IssueDetailActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_detail);

        if (getIntent().getExtras() != null) {
            number = getIntent().getExtras().getInt(NUMBER);
            owner = getIntent().getExtras().getString(OWNER);
            repo = getIntent().getExtras().getString(REPO);

            findViews();
            setPreviewData();
        }
    }

    private void findViews() {
        smoothBar = (SmoothProgressBar) findViewById(R.id.smoothBar);
        fabLayout = (FABCenterLayout) findViewById(R.id.fabLayout);
        fabLayout.setFabClickListener(new FabClickListener());

        headerView = findViewById(R.id.top);
        discussionView = findViewById(R.id.discussionFeed);

        TextDrawable drawable = new TextDrawable(this, "+");
        drawable.color(Color.WHITE);
        drawable.sizeDp(30);
        fabLayout.setFABDrawable(drawable);

    }

    private void setPreviewData() {
        if (getActionBar() != null) {
            getActionBar().setTitle(repo);
            getActionBar().setSubtitle(Html.fromHtml(getString(R.string.issue_detail_title, number)));
        }

        if (issueInfoFragment == null) {
            issueInfoFragment = new IssueDetailInfoFragment();
        }

        if (issueDiscussionFragment == null) {
            issueDiscussionFragment = IssueDiscussionFragment.newInstance(owner, repo, number);
            issueDiscussionFragment.setRefreshListener(this);
        }

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.issueDetailInfo, issueInfoFragment);
        ft.replace(R.id.discussionFeed, issueDiscussionFragment);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.issue_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        return true;
    }

    @Override
    public void showRefresh() {
        if (smoothBar != null) {
            smoothBar.progressiveStart();
        }
    }

    @Override
    public void cancelRefresh() {
        if (smoothBar != null) {
            smoothBar.progressiveStop();
        }
    }

    @Override
    public boolean hasPermissionPull() {
        return false;
    }

    @Override
    public boolean hasPermissionPush() {
        return false;
    }

    @Override
    public boolean hasPermissionAdmin() {
        return false;
    }

    private class FabClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

        }
    }
}

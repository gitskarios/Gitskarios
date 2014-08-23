package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabTitle;

import com.alorma.github.R;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.adapter.detail.issue.IssueDetailPagerAdapter;
import com.alorma.github.ui.fragment.ActionRepoListener;
import com.alorma.github.ui.listeners.RefreshListener;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class IssueDetailActivity extends BackActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, ActionRepoListener, RefreshListener {

    private static final String NUMBER = "NUMBER";
    public static final String OWNER = "OWNER";
    public static final String REPO = "REPO";
    private String owner;
    private String repo;
    private int number;
    private TabTitle tabInfo;
    private TabTitle tabDiscussion;
    private ViewPager pager;
    private IssueDetailPagerAdapter pagerAdapter;
    private SmoothProgressBar smoothBar;

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
        tabInfo = (TabTitle) findViewById(R.id.tabInfo);
        tabDiscussion = (TabTitle) findViewById(R.id.tabDiscussion);
        smoothBar = (SmoothProgressBar) findViewById(R.id.smoothBar);

        pager = (ViewPager) findViewById(R.id.pager);

        tabInfo.setOnClickListener(this);
        tabDiscussion.setOnClickListener(this);

        tabInfo.setSelected(true);
    }

    private void setPreviewData() {
        if (getActionBar() != null) {
            getActionBar().setTitle(getString(R.string.issue_detail_title, repo, number));
        }

        pagerAdapter = new IssueDetailPagerAdapter(getFragmentManager(), owner, repo, number);
        pagerAdapter.setRefreshListener(this);
        pagerAdapter.setActionRepoListener(this);
        pager.setAdapter(pagerAdapter);
        pager.setOnPageChangeListener(this);
    }

    private void setData() {
        /*
        if (user != null) {
            autorTv.setText(Html.fromHtml(getString(R.string.issue_created_by, user.login)));
            ImageLoader instance = ImageLoader.getInstance();
            instance.displayImage(user.avatar_url, avatarIv);
        }

        int colorState = getResources().getColor(R.color.issue_state_close);
        if (IssueState.open == issueState) {
            colorState = getResources().getColor(R.color.issue_state_open);
        }

        stateV.setBackgroundColor(colorState);
        numTv.setTextColor(colorState);

        if (isPullRequest) {
            IconDrawable iconDrawable = new IconDrawable(this, Iconify.IconValue.fa_code_fork);
            iconDrawable.colorRes(R.color.gray_github_medium);
            pullRequestIv.setImageDrawable(iconDrawable);
        } else {
            IconDrawable iconDrawable = new IconDrawable(this, Iconify.IconValue.fa_info_circle);
            iconDrawable.colorRes(R.color.gray_github_light_selected);
            pullRequestIv.setImageDrawable(iconDrawable);
        }*/
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tabInfo:
                tabInfo.setSelected(true);
                tabDiscussion.setSelected(false);
                pager.setCurrentItem(0);
                break;
            case R.id.tabDiscussion:
                tabInfo.setSelected(false);
                tabDiscussion.setSelected(true);
                pager.setCurrentItem(1);
                break;
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
        switch (i) {
            case 0:
                tabInfo.setSelected(true);
                tabDiscussion.setSelected(false);
                break;
            case 1:
                tabInfo.setSelected(false);
                tabDiscussion.setSelected(true);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

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
}

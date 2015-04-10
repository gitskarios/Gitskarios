package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.IssueState;
import com.alorma.github.sdk.bean.dto.response.Permissions;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.bean.issue.IssueStory;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.issues.story.IssueStoryLoader;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.adapter.issues.IssueDetailAdapter;
import com.alorma.github.ui.fragment.detail.issue.IssueDiscussionFragment;
import com.alorma.github.utils.AttributesUtils;
import com.alorma.githubicons.GithubIconDrawable;
import com.alorma.githubicons.GithubIconify;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.mrengineer13.snackbar.SnackBar;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ValueAnimator;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class IssueDetailActivity extends BackActivity implements BaseClient.OnResultCallback<IssueStory>, View.OnClickListener /*, BaseClient.OnResultCallback<Issue>, */ {

    public static final String ISSUE_INFO = "ISSUE_INFO";
    public static final String PERMISSIONS = "PERMISSIONS";
    private static final int NEW_COMMENT_REQUEST = 1243;

    private IssueDiscussionFragment issueDiscussionFragment;
    private IssueState issueState;
    private Permissions permissions;
    private boolean shouldRefreshOnBack;
    private IssueInfo issueInfo;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;

    public static Intent createLauncherIntent(Context context, IssueInfo issueInfo, Permissions permissions) {
        Bundle bundle = new Bundle();

        bundle.putParcelable(ISSUE_INFO, issueInfo);
        bundle.putParcelable(PERMISSIONS, permissions);

        Intent intent = new Intent(context, IssueDetailActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_detail);


        if (getIntent().getExtras() != null) {
            issueInfo = getIntent().getExtras().getParcelable(ISSUE_INFO);
            permissions = getIntent().getExtras().getParcelable(PERMISSIONS);


/*
            issueInfo.repo.owner = "forkhubs";
            issueInfo.repo.name = "android";
            issueInfo.num = 736;
*/


            findViews();
        }
    }

    private void findViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fab = (FloatingActionButton) findViewById(R.id.fabButton);

        GithubIconDrawable drawable = new GithubIconDrawable(this, GithubIconify.IconValue.octicon_comment_discussion).color(Color.WHITE).fabSize();

        fab.setIconDrawable(drawable);
    }

    @Override
    protected void getContent() {
        super.getContent();
        IssueStoryLoader issueStoryLoader = new IssueStoryLoader(this, issueInfo);
        issueStoryLoader.setOnResultCallback(this);
        issueStoryLoader.execute();
    }

    @Override
    public void onResponseOk(IssueStory issueStory, Response r) {
        changeColor(issueStory.issue);

        fab.setVisibility(issueStory.issue.locked ? View.GONE : View.VISIBLE);
        fab.setOnClickListener(issueStory.issue.locked ? null : this);

        if (getSupportActionBar() != null) {
            String issueName = issueInfo.repo.name;
            if (issueStory.issue.pullRequest != null) {
                getSupportActionBar().setSubtitle(getString(R.string.pull_requests_subtitle, issueName));
            } else {
                getSupportActionBar().setSubtitle(getString(R.string.issue_subtitle, issueName));
            }
        }
        String status = getString(R.string.issue_status_open);
        if (IssueState.closed == issueStory.issue.state) {
            status = getString(R.string.issue_status_close);
        }
        setTitle("#" + issueStory.issue.number + " " + status);
        IssueDetailAdapter adapter = new IssueDetailAdapter(this, getLayoutInflater(), issueStory);
        recyclerView.setAdapter(adapter);
    }

    private void changeColor(Issue issue) {
        int colorState = getResources().getColor(R.color.issue_state_close);
        int colorStateDark = getResources().getColor(R.color.issue_state_close_dark);
        if (IssueState.open == issue.state) {
            colorState = getResources().getColor(R.color.issue_state_open);
            colorStateDark = getResources().getColor(R.color.issue_state_open_dark);
        }

        int primary = getResources().getColor(R.color.primary_alpha);
        int accent = getResources().getColor(R.color.repos_accent);
        int accentDark = getResources().getColor(R.color.repos_accent_dark);
        int primaryDark = getResources().getColor(R.color.repos_primary_dark_alpha);

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), primary, colorState);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                int color = (Integer) animator.getAnimatedValue();
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
                }
            }

        });

        ValueAnimator colorAnimationFab = ValueAnimator.ofObject(new ArgbEvaluator(), accent, colorState);
        colorAnimationFab.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                int color = (Integer) animator.getAnimatedValue();

                if (fab != null) {
                    fab.setColorNormal(color);
                }

            }

        });

        ValueAnimator colorAnimationFabPressed = ValueAnimator.ofObject(new ArgbEvaluator(), accentDark, colorStateDark);
        colorAnimationFabPressed.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                int color = (Integer) animator.getAnimatedValue();

                if (fab != null) {
                    fab.setColorPressed(color);
                }

            }

        });

        ValueAnimator colorAnimationStatus = ValueAnimator.ofObject(new ArgbEvaluator(), primaryDark, colorStateDark);
        colorAnimationStatus.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                int color = (Integer) animator.getAnimatedValue();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(color);
                }
            }

        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(1000);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.playTogether(colorAnimation, colorAnimationStatus, colorAnimationFab, colorAnimationFabPressed);
        animatorSet.start();
    }

    @Override
    public void onFail(RetrofitError error) {
        try {
            new SnackBar.Builder(this).withMessage(error.getResponse().getReason()).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {

    }


/*



    private void setData() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(issue.title);
        }

        addDiscussionFragment();
    }

    private void addDiscussionFragment() {
        try {
            if (!isFinishing()) {
                issueDiscussionFragment = IssueDiscussionFragment.newInstance(issueInfo);

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.discussionFeed, issueDiscussionFragment);
                ft.commit();
            }
        } catch (IllegalStateException e) {
            Crashlytics.logException(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.issue_detail, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();

        if (permissions != null && permissions.push && issueState == IssueState.open) {
            MenuItem menuItem = menu.add(0, R.id.action_close_issue, 1, getString(R.string.closeIssue));
            menuItem.setIcon(new GithubIconDrawable(this, GithubIconify.IconValue.octicon_x).actionBarSize().colorRes(R.color.white));
            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

        MenuItem menuItem;
        GithubIconify.IconValue icon;
        if (fabLayout.isFold()) {
            menuItem = menu.add(0, R.id.action_fold_issue, 0, R.string.unfold_issue);
            icon = GithubIconify.IconValue.octicon_unfold;
        } else {
            menuItem = menu.add(0, R.id.action_fold_issue, 0, R.string.fold_issue);
            icon = GithubIconify.IconValue.octicon_fold;
        }
        menuItem.setIcon(new GithubIconDrawable(this, icon).actionBarSize().colorRes(R.color.white));
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(shouldRefreshOnBack ? RESULT_FIRST_USER : RESULT_OK);
                finish();
                break;
            case R.id.action_close_issue:
                closeIssueDialog();
                break;
            case R.id.action_fold_issue:
                if (fabLayout.isFold()) {
                    issueBody.setMaxLines(Integer.MAX_VALUE);
                    issueBody.setEllipsize(null);
                } else {
                    issueBody.setMaxLines(3);
                    issueBody.setEllipsize(TextUtils.TruncateAt.END);
                }
                fabLayout.setFold(!fabLayout.isFold());
                invalidateOptionsMenu();
                break;
        }

        return true;
    }

    public void onAddComment() {
        Intent intent = NewIssueCommentActivity.launchIntent(IssueDetailActivity.this, issueInfo);
        startActivityForResult(intent, NEW_COMMENT_REQUEST);
    }

    private void closeIssueDialog() {
        String title = getString(R.string.closeIssue);
        String accept = getString(R.string.accept);
        String cancel = getString(R.string.cancel);

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(title)
                .positiveText(accept)
                .negativeText(cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog materialDialog) {
                        super.onPositive(materialDialog);
                        closeIssue();
                    }
                })
                .build();

        dialog.show();
    }

    private void closeIssue() {
        CloseIssueClient closeIssueClient = new CloseIssueClient(this, issueInfo.repo.owner, issueInfo.repo.name, issueInfo.num);
        closeIssueClient.setOnResultCallback(this);
        closeIssueClient.execute();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == fabLayout.getFabId()) {
            onAddComment();
        }
    }

    @Override
    public void onResponseOk(Issue issue, Response r) {
        if (issue != null) {
            issueBody.setText(issue.body);
            IssueDetailActivity.this.issue = issue;
            IssueDetailActivity.this.issueState = issue.state;
            invalidateOptionsMenu();
            setData();

            //Spanned issueNumber = Html.fromHtml(getString(R.string.issue_detail_title, issue.number));

            shouldRefreshOnBack = true;
        }
    }

    @Override
    public void onFail(RetrofitError error) {
        ErrorHandler.onRetrofitError(IssueDetailActivity.this, "Closing issue: ", error);
    }

    @Override
    public void onBackPressed() {
        setResult(shouldRefreshOnBack ? RESULT_FIRST_USER : RESULT_OK);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == NEW_COMMENT_REQUEST) {
                addDiscussionFragment();
            }
        }
    }*/
}

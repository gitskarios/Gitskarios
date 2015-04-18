package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.request.CreateMilestoneRequestDTO;
import com.alorma.github.sdk.bean.dto.request.EditIssueMilestoneRequestDTO;
import com.alorma.github.sdk.bean.dto.request.EditIssueRequestDTO;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.IssueState;
import com.alorma.github.sdk.bean.dto.response.Milestone;
import com.alorma.github.sdk.bean.dto.response.Permissions;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.bean.issue.IssueStory;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.issues.CloseIssueClient;
import com.alorma.github.sdk.services.issues.CreateMilestoneClient;
import com.alorma.github.sdk.services.issues.EditIssueClient;
import com.alorma.github.sdk.services.issues.GetMilestonesClient;
import com.alorma.github.sdk.services.issues.story.IssueStoryLoader;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.adapter.issues.IssueDetailAdapter;
import com.alorma.github.ui.dialog.NewIssueCommentActivity;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.mrengineer13.snackbar.SnackBar;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ValueAnimator;

import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class IssueDetailActivity extends BackActivity implements BaseClient.OnResultCallback<IssueStory>, View.OnClickListener {

    public static final String ISSUE_INFO = "ISSUE_INFO";
    public static final String PERMISSIONS = "PERMISSIONS";
    private static final int NEW_COMMENT_REQUEST = 1243;

    private Permissions permissions;
    private boolean shouldRefreshOnBack;
    private IssueInfo issueInfo;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private IssueStory issueStory;

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

            findViews();
        }
    }

    private void findViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fab = (FloatingActionButton) findViewById(R.id.fabButton);

        IconicsDrawable drawable = new IconicsDrawable(this, Octicons.Icon.oct_comment_discussion).color(Color.WHITE).sizeDp(24);

        fab.setIconDrawable(drawable);
    }

    @Override
    protected void getContent() {
        super.getContent();
        showProgressDialog(R.style.SpotDialog_loading_issue);
        IssueStoryLoader issueStoryLoader = new IssueStoryLoader(this, issueInfo);
        issueStoryLoader.setOnResultCallback(this);
        issueStoryLoader.execute();
    }

    @Override
    public void onResponseOk(IssueStory issueStory, Response r) {
        hideProgressDialog();
        this.issueStory = issueStory;
        applyIssue();
    }

    private void applyIssue() {
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

        invalidateOptionsMenu();
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
        if (view.getId() == fab.getId()) {
            if (!issueStory.issue.locked) {
                onAddComment();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (this.issueStory != null) {
            if (permissions != null && permissions.push) {
                getMenuInflater().inflate(R.menu.issue_detail, menu);
            }
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (this.issueStory != null) {

            if (issueStory.issue.state == IssueState.closed) {
                if (menu.findItem(R.id.action_close_issue) != null) {
                    menu.removeItem(R.id.action_close_issue);
                }
            } else {
                if (permissions != null && permissions.push) {
                    if (menu.findItem(R.id.action_close_issue) != null) {
                        menu.removeItem(R.id.action_close_issue);
                    }
                    MenuItem menuItem = menu.add(0, R.id.action_close_issue, 1, getString(R.string.closeIssue));
                    menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                }
            }
        }

        return true;
    }

    public void onAddComment() {
        Intent intent = NewIssueCommentActivity.launchIntent(IssueDetailActivity.this, issueInfo);
        startActivityForResult(intent, NEW_COMMENT_REQUEST);
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
                invalidateOptionsMenu();
                break;
            case R.id.issue_edit_milestone:
                editMilestone();
                break;
        }

        return true;
    }

    private void editMilestone() {
        GetMilestonesClient milestonesClient = new GetMilestonesClient(this, issueInfo);
        milestonesClient.setOnResultCallback(new MilestonesCallback());
        milestonesClient.execute();

        showProgressDialog(R.style.SpotDialog_loading_milestones);
    }

    private class MilestonesCallback implements BaseClient.OnResultCallback<List<Milestone>> {
        @Override
        public void onResponseOk(final List<Milestone> milestones, Response r) {
            hideProgressDialog();
            if (milestones.size() == 0) {
                showCreateMilestone();
            } else {
                String[] itemsMilestones = new String[milestones.size()];

                for (int i = 0; i < milestones.size(); i++) {
                    itemsMilestones[i] = milestones.get(i).title;
                }

                int selectedMilestone = -1;
                for (int i = 0; i < milestones.size(); i++) {
                    if (IssueDetailActivity.this.issueStory.issue.milestone != null) {
                        String currentMilestone = IssueDetailActivity.this.issueStory.issue.milestone.title;
                        if (currentMilestone != null && currentMilestone.equals(milestones.get(i).title)) {
                            selectedMilestone = i;
                            break;
                        }
                    }
                }

                MaterialDialog.Builder builder = new MaterialDialog.Builder(IssueDetailActivity.this)
                        .title(R.string.select_milestone)
                        .items(itemsMilestones)
                        .itemsCallbackSingleChoice(selectedMilestone, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence text) {

                                addMilestone(milestones.get(i));

                                return false;
                            }
                        })
                        .forceStacking(true)
                        .widgetColorRes(R.color.primary)
                        .negativeText(R.string.add_milestone);

                if (selectedMilestone != -1) {
                    builder.neutralText(R.string.clear_milestone);
                }

                builder.callback(new MaterialDialog.ButtonCallback() {

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        showCreateMilestone();
                    }

                    @Override
                    public void onNeutral(MaterialDialog dialog) {
                        super.onNeutral(dialog);
                        clearMilestone();
                    }
                });

                builder.show();
            }
        }

        @Override
        public void onFail(RetrofitError error) {

        }
    }

    private void showCreateMilestone() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        builder.title(R.string.add_milestone);
        builder.content(R.string.add_milestone_description);
        builder.input(R.string.add_milestone_hint, 0, new MaterialDialog.InputCallback() {
            @Override
            public void onInput(MaterialDialog materialDialog, CharSequence milestoneName) {
                createMilestone(milestoneName.toString());
            }
        })
                .negativeText(R.string.cancel);

        builder.show();
    }

    private void createMilestone(String milestoneName) {
        CreateMilestoneRequestDTO createMilestoneRequestDTO = new CreateMilestoneRequestDTO(milestoneName);

        CreateMilestoneClient createMilestoneClient = new CreateMilestoneClient(this, issueInfo.repo, createMilestoneRequestDTO);
        createMilestoneClient.setOnResultCallback(new BaseClient.OnResultCallback<Milestone>() {
            @Override
            public void onResponseOk(Milestone milestone, Response r) {
                addMilestone(milestone);
            }

            @Override
            public void onFail(RetrofitError error) {
                hideProgressDialog();
            }
        });
        createMilestoneClient.execute();
    }

    private void addMilestone(Milestone milestone) {
        showProgressDialog(R.style.SpotDialog_loading_adding_milestones);
        EditIssueMilestoneRequestDTO editIssueRequestDTO = new EditIssueMilestoneRequestDTO();
        editIssueRequestDTO.milestone = milestone.number;
        EditIssueClient client = new EditIssueClient(IssueDetailActivity.this, issueInfo, editIssueRequestDTO);
        client.setOnResultCallback(new BaseClient.OnResultCallback<Issue>() {
            @Override
            public void onResponseOk(Issue issue, Response r) {
                hideProgressDialog();
                getContent();
            }

            @Override
            public void onFail(RetrofitError error) {
                hideProgressDialog();
            }
        });
        client.execute();
    }

    private void clearMilestone() {
        showProgressDialog(R.style.SpotDialog_clear_milestones);
        EditIssueMilestoneRequestDTO editIssueRequestDTO = new EditIssueMilestoneRequestDTO();
        editIssueRequestDTO.milestone = null;
        EditIssueClient client = new EditIssueClient(IssueDetailActivity.this, issueInfo, editIssueRequestDTO);
        client.setOnResultCallback(new BaseClient.OnResultCallback<Issue>() {
            @Override
            public void onResponseOk(Issue issue, Response r) {
                hideProgressDialog();
                getContent();
            }

            @Override
            public void onFail(RetrofitError error) {
                hideProgressDialog();
            }
        });
        client.execute();
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
        closeIssueClient.setOnResultCallback(new BaseClient.OnResultCallback<Issue>() {
            @Override
            public void onResponseOk(Issue issue, Response r) {
                if (issue != null) {
                    getContent();
                    IssueDetailActivity.this.issueStory.issue = issue;
                    shouldRefreshOnBack = true;
                }
            }

            @Override
            public void onFail(RetrofitError error) {

            }
        });
        closeIssueClient.execute();
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
                getContent();
            }
        }
    }
}

package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alorma.github.GitskariosSettings;
import com.alorma.github.R;
import com.alorma.github.StoreCredentials;
import com.alorma.github.sdk.bean.dto.request.CreateMilestoneRequestDTO;
import com.alorma.github.sdk.bean.dto.request.EditIssueBodyRequestDTO;
import com.alorma.github.sdk.bean.dto.request.EditIssueLabelsRequestDTO;
import com.alorma.github.sdk.bean.dto.request.EditIssueMilestoneRequestDTO;
import com.alorma.github.sdk.bean.dto.request.EditIssueRequestDTO;
import com.alorma.github.sdk.bean.dto.request.EditIssueTitleRequestDTO;
import com.alorma.github.sdk.bean.dto.request.MergeButtonRequest;
import com.alorma.github.sdk.bean.dto.response.GithubComment;
import com.alorma.github.sdk.bean.dto.response.GithubStatusResponse;
import com.alorma.github.sdk.bean.dto.response.Head;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.IssueState;
import com.alorma.github.sdk.bean.dto.response.Label;
import com.alorma.github.sdk.bean.dto.response.MergeButtonResponse;
import com.alorma.github.sdk.bean.dto.response.Milestone;
import com.alorma.github.sdk.bean.dto.response.MilestoneState;
import com.alorma.github.sdk.bean.dto.response.PullRequest;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.bean.issue.PullRequestStory;
import com.alorma.github.sdk.services.issues.CreateMilestoneClient;
import com.alorma.github.sdk.services.issues.EditIssueClient;
import com.alorma.github.sdk.services.issues.GetMilestonesClient;
import com.alorma.github.sdk.services.issues.GithubIssueLabelsClient;
import com.alorma.github.sdk.services.pullrequest.MergePullRequestClient;
import com.alorma.github.sdk.services.pullrequest.story.PullRequestStoryLoader;
import com.alorma.github.sdk.services.repo.GetRepoClient;
import com.alorma.github.sdk.services.repo.GetShaCombinedStatus;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.actions.ActionCallback;
import com.alorma.github.ui.actions.AddIssueCommentAction;
import com.alorma.github.ui.actions.ChangeAssigneeAction;
import com.alorma.github.ui.actions.CloseAction;
import com.alorma.github.ui.actions.ReopenAction;
import com.alorma.github.ui.actions.ShareAction;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.adapter.issues.PullRequestDetailAdapter;
import com.alorma.github.ui.listeners.IssueDetailRequestListener;
import com.alorma.github.ui.view.pullrequest.PullRequestDetailView;
import com.alorma.github.utils.ShortcutUtils;
import com.alorma.gitskarios.core.Pair;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ValueAnimator;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class PullRequestDetailActivity extends BackActivity
        implements View.OnClickListener, PullRequestDetailView.PullRequestActionsListener, IssueDetailRequestListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String ISSUE_INFO = "ISSUE_INFO";
    public static final String ISSUE_INFO_REPO_NAME = "ISSUE_INFO_REPO_NAME";
    public static final String ISSUE_INFO_REPO_OWNER = "ISSUE_INFO_REPO_OWNER";
    public static final String ISSUE_INFO_NUMBER = "ISSUE_INFO_NUMBER";

    private static final int NEW_COMMENT_REQUEST = 1243;
    private static final int ISSUE_BODY_EDIT = 4252;
    Subscriber<GithubComment> githubCommentSubscriber = new Subscriber<GithubComment>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(GithubComment githubComment) {

        }
    };
    private boolean shouldRefreshOnBack;
    private IssueInfo issueInfo;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private PullRequestStory pullRequestStory;
    private int primary;
    private int primaryDark;
    private ProgressBar loadingView;
    private Repo repository;
    private PullRequestDetailAdapter adapter;
    private SwipeRefreshLayout swipe;

    public static Intent createLauncherIntent(Context context, IssueInfo issueInfo) {
        Bundle bundle = new Bundle();

        bundle.putParcelable(ISSUE_INFO, issueInfo);

        Intent intent = new Intent(context, PullRequestDetailActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    public static Intent createShortcutLauncherIntent(Context context, IssueInfo issueInfo) {
        Bundle bundle = new Bundle();

        bundle.putString(ISSUE_INFO_REPO_NAME, issueInfo.repoInfo.name);
        bundle.putString(ISSUE_INFO_REPO_OWNER, issueInfo.repoInfo.owner);
        bundle.putInt(ISSUE_INFO_NUMBER, issueInfo.num);

        Intent intent = new Intent(context, PullRequestDetailActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pullrequest_detail);

        if (getIntent().getExtras() != null) {

            issueInfo = (IssueInfo) getIntent().getExtras().getParcelable(ISSUE_INFO);

            if (issueInfo == null && getIntent().getExtras().containsKey(ISSUE_INFO_NUMBER)) {
                String name = getIntent().getExtras().getString(ISSUE_INFO_REPO_NAME);
                String owner = getIntent().getExtras().getString(ISSUE_INFO_REPO_OWNER);

                RepoInfo repoInfo = new RepoInfo();
                repoInfo.name = name;
                repoInfo.owner = owner;

                int num = getIntent().getExtras().getInt(ISSUE_INFO_NUMBER);

                issueInfo = new IssueInfo();
                issueInfo.repoInfo = repoInfo;
                issueInfo.num = num;
            }

            primary = getResources().getColor(R.color.primary);
            primaryDark = getResources().getColor(R.color.primary_dark_alpha);

            findViews();
        }
    }

    private void checkEditTitle() {
        if (issueInfo != null && pullRequestStory != null && pullRequestStory.pullRequest != null) {

            StoreCredentials credentials = new StoreCredentials(this);

            GitskariosSettings settings = new GitskariosSettings(this);
            if (settings.shouldShowDialogEditIssue()) {
                if (issueInfo.repoInfo.permissions != null && issueInfo.repoInfo.permissions.push) {
                    showEditDialog(R.string.dialog_edit_issue_edit_title_and_body_by_owner);
                } else if (pullRequestStory.pullRequest.user.login.equals(credentials.getUserName())) {
                    showEditDialog(R.string.dialog_edit_issue_edit_title_and_body_by_author);
                }
            }
        }
    }

    private void showEditDialog(int content) {
        new MaterialDialog.Builder(this).title(R.string.dialog_edit_issue).content(content).positiveText(R.string.ok).show();
    }

    private void findViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fab = (FloatingActionButton) findViewById(R.id.fabButton);

        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipe.setColorSchemeResources(R.color.accent);

        loadingView = (ProgressBar) findViewById(R.id.loading_view);

        IconicsDrawable drawable = new IconicsDrawable(this, Octicons.Icon.oct_comment_discussion).color(Color.WHITE).sizeDp(24);

        fab.setImageDrawable(drawable);

        ViewCompat.setElevation(getToolbar(), getResources().getDimensionPixelOffset(R.dimen.gapSmall));
    }

    @Override
    protected void getContent() {
        super.getContent();

        hideProgressDialog();

        loadingView.setVisibility(View.VISIBLE);
        if (checkPermissions(issueInfo)) {
            GetRepoClient repoClient = new GetRepoClient(issueInfo.repoInfo);
            repoClient.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Repo>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(Repo repo) {
                    issueInfo.repoInfo.permissions = repo.permissions;
                    repository = repo;

                    loadPullRequest();
                }
            });
        } else {
            loadPullRequest();
        }
    }

    private boolean checkPermissions(IssueInfo issueInfo) {
        return issueInfo != null && issueInfo.repoInfo != null && issueInfo.repoInfo.permissions == null;
    }

    private void loadPullRequest() {
        PullRequestStoryLoader pullRequestStoryLoader = new PullRequestStoryLoader(issueInfo);
        pullRequestStoryLoader.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<PullRequestStory>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                showError();
            }

            @Override
            public void onNext(PullRequestStory pullRequestStory) {
                onResponseOk(pullRequestStory);
            }
        });
    }

    private void showError() {
        hideProgressDialog();
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        builder.title(R.string.ups);
        builder.content(getString(R.string.issue_detail_error, issueInfo.toString()));
        builder.positiveText(R.string.retry);
        builder.negativeText(R.string.accept);
        builder.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                super.onPositive(dialog);
                getContent();
            }

            @Override
            public void onNegative(MaterialDialog dialog) {
                super.onNegative(dialog);
                finish();
            }
        });
        builder.show();
    }

    public void onResponseOk(final PullRequestStory pullRequestStory) {
        hideProgressDialog();
        this.pullRequestStory = pullRequestStory;
        this.pullRequestStory.pullRequest.repository = repository;


        swipe.setRefreshing(false);
        swipe.setOnRefreshListener(this);

        GetShaCombinedStatus status = new GetShaCombinedStatus(issueInfo.repoInfo, pullRequestStory.pullRequest.head.ref);
        status.observable().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Pair<GithubStatusResponse, Integer>, GithubStatusResponse>() {
                    @Override
                    public GithubStatusResponse call(Pair<GithubStatusResponse, Integer> githubStatusResponseIntegerPair) {
                        return githubStatusResponseIntegerPair.first;
                    }
                })
                .subscribe(new Subscriber<GithubStatusResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(GithubStatusResponse githubStatusResponse) {
                        pullRequestStory.statusResponse = githubStatusResponse;
                        adapter.notifyDataSetChanged();
                    }
                });

        applyIssue();
    }

    private void applyIssue() {
        loadingView.setVisibility(View.GONE);
        checkEditTitle();
        changeColor(pullRequestStory.pullRequest);

        fab.setVisibility(pullRequestStory.pullRequest.locked ? View.GONE : View.VISIBLE);
        fab.setOnClickListener(pullRequestStory.pullRequest.locked ? null : this);

        String status = getString(R.string.issue_status_open);
        if (IssueState.closed == pullRequestStory.pullRequest.state) {
            status = getString(R.string.issue_status_close);
        } else if (pullRequestStory.pullRequest.merged) {
            status = getString(R.string.pullrequest_status_merged);
        }
        setTitle("#" + pullRequestStory.pullRequest.number + " " + status);
        adapter =
                new PullRequestDetailAdapter(this, getLayoutInflater(), pullRequestStory, issueInfo.repoInfo, issueInfo.repoInfo.permissions, this);
        adapter.setIssueDetailRequestListener(this);
        recyclerView.setAdapter(adapter);

        invalidateOptionsMenu();
    }

    private void changeColor(PullRequest pullRequest) {
        int colorState = getResources().getColor(R.color.pullrequest_state_close);
        int colorStateDark = getResources().getColor(R.color.pullrequest_state_close_dark);
        if (IssueState.open == pullRequest.state) {
            colorState = getResources().getColor(R.color.pullrequest_state_open);
            colorStateDark = getResources().getColor(R.color.pullrequest_state_open_dark);
        } else if (pullRequest.merged) {
            colorState = getResources().getColor(R.color.pullrequest_state_merged);
            colorStateDark = getResources().getColor(R.color.pullrequest_state_merged);
        }

        swipe.setColorSchemeColors(colorState);

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), primary, colorState);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                int color = (Integer) animator.getAnimatedValue();
                if (getToolbar() != null) {
                    getToolbar().setBackgroundColor(color);
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
        animatorSet.setDuration(750);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());

        animatorSet.playTogether(colorAnimation, colorAnimationStatus);

        final int finalColorState = colorState;
        final int finalColorStateDark = colorStateDark;
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                primary = finalColorState;
                primaryDark = finalColorStateDark;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == fab.getId()) {
            if (!pullRequestStory.pullRequest.locked) {
                onAddComment();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (this.pullRequestStory != null) {
            if (issueInfo.repoInfo.permissions != null && issueInfo.repoInfo.permissions.push) {
                getMenuInflater().inflate(R.menu.pullrequest_detail, menu);
            } else {
                getMenuInflater().inflate(R.menu.pullrequest_detail_no_permissions, menu);
            }

            MenuItem itemShare = menu.findItem(R.id.share_issue);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                itemShare.setIcon(getResources().getDrawable(R.drawable.ic_menu_share_mtrl_alpha, getTheme()));
            } else {
                itemShare.setIcon(getResources().getDrawable(R.drawable.ic_menu_share_mtrl_alpha));
            }
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (this.pullRequestStory != null) {

            if (issueInfo.repoInfo.permissions != null && issueInfo.repoInfo.permissions.push) {
                if (menu.findItem(R.id.action_close_issue) != null) {
                    menu.removeItem(R.id.action_close_issue);
                }
                if (menu.findItem(R.id.action_reopen_issue) != null) {
                    menu.removeItem(R.id.action_reopen_issue);
                }
                if (pullRequestStory.pullRequest.state == IssueState.closed) {
                    MenuItem menuItem = menu.add(0, R.id.action_reopen_issue, 1, getString(R.string.reopenPullrequst));
                    menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                } else {
                    MenuItem menuItem = menu.add(0, R.id.action_close_issue, 1, getString(R.string.closePullRequest));
                    menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                }
            }
        }

        return true;
    }

    public void onAddComment() {
        String hint = getString(R.string.add_comment);
        Intent intent = ContentEditorActivity.createLauncherIntent(this, issueInfo.repoInfo, issueInfo.num, hint, null, false, false);
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
                new CloseAction(this, issueInfo, R.string.closePullRequest).setCallback(new ActionCallback<Issue>() {
                    @Override
                    public void onResult(Issue issue) {
                        getContent();
                        Snackbar.make(fab, R.string.pullrequest_change, Snackbar.LENGTH_SHORT).show();
                    }
                }).execute();
                break;
            case R.id.action_reopen_issue:
                new ReopenAction(this, issueInfo, R.string.reopenPullrequst).setCallback(new ActionCallback<Issue>() {
                    @Override
                    public void onResult(Issue issue) {
                        getContent();
                        Snackbar.make(fab, R.string.issue_change, Snackbar.LENGTH_SHORT).show();
                    }
                }).execute();
                break;
            case R.id.issue_edit_milestone:
                editMilestone();
                break;
            case R.id.issue_edit_assignee:
                new ChangeAssigneeAction(this, issueInfo).setCallback(new AssigneActionCallback()).execute();
                break;
            case R.id.issue_edit_labels:
                openLabels();
                break;
            case R.id.share_issue:
                if (pullRequestStory != null && pullRequestStory.pullRequest != null) {
                    String title = issueInfo.toString();
                    String url = pullRequestStory.pullRequest.html_url;
                    new ShareAction(this, title, url).execute();
                }
                break;
            case R.id.action_add_shortcut:
                ShortcutUtils.addPrShortcut(this, issueInfo);
                break;
        }

        return true;
    }

    private void editMilestone() {
        GetMilestonesClient milestonesClient = new GetMilestonesClient(issueInfo.repoInfo, MilestoneState.open, true);
        milestonesClient.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Milestone>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<Milestone> milestones) {
                onMilestonesLoaded(milestones);
            }
        });

        showProgressDialog(R.string.loading_milestones);
    }

    @Override
    public void onTitleEditRequest() {
        new MaterialDialog.Builder(this).title(R.string.edit_issue_title)
                .input(null, pullRequestStory.pullRequest.title, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {

                        EditIssueTitleRequestDTO editIssueTitleRequestDTO = new EditIssueTitleRequestDTO();
                        editIssueTitleRequestDTO.title = charSequence.toString();
                        executeEditIssue(editIssueTitleRequestDTO, R.string.issue_change_title);
                    }
                })
                .positiveText(R.string.edit_issue_button_ok)
                .neutralText(R.string.edit_issue_button_neutral)
                .show();
    }

    @Override
    public void onContentEditRequest() {
        String body = pullRequestStory.pullRequest.body != null ? pullRequestStory.pullRequest.body.replace("\n", "<br />") : "";
        Intent launcherIntent =
                ContentEditorActivity.createLauncherIntent(this, issueInfo.repoInfo, issueInfo.num, getString(R.string.edit_issue_body_hint), body,
                        true, false);
        startActivityForResult(launcherIntent, ISSUE_BODY_EDIT);
    }

    @Override
    public void mergeRequest(final Head head, Head base) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        builder.title(R.string.merge_title);
        builder.content(head.label);
        builder.input(getString(R.string.merge_message), pullRequestStory.pullRequest.title, false
                , new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                        merge(charSequence.toString(), head.sha, issueInfo);
                    }
                });
        builder.inputType(InputType.TYPE_CLASS_TEXT);
        builder.show();
    }

    private void merge(String message, String sha, IssueInfo issueInfo) {
        showProgressDialog(R.string.merging);
        MergeButtonRequest mergeButtonRequest = new MergeButtonRequest();
        mergeButtonRequest.commit_message = message;
        mergeButtonRequest.sha = sha;
        MergePullRequestClient mergePullRequestClient = new MergePullRequestClient(issueInfo, mergeButtonRequest);
        mergePullRequestClient.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<MergeButtonResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                hideProgressDialog();
                Snackbar.make(fab, R.string.pull_requests_merged_failed, Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(MergeButtonResponse mergeButtonResponse) {
                hideProgressDialog();
                Snackbar.make(fab, R.string.pull_requests_merged, Snackbar.LENGTH_SHORT).show();
                reload();
            }
        });
    }

    private void onMilestonesLoaded(final List<Milestone> milestones) {
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
                if (PullRequestDetailActivity.this.pullRequestStory.pullRequest.milestone != null) {
                    String currentMilestone = PullRequestDetailActivity.this.pullRequestStory.pullRequest.milestone.title;
                    if (currentMilestone != null && currentMilestone.equals(milestones.get(i).title)) {
                        selectedMilestone = i;
                        break;
                    }
                }
            }

            MaterialDialog.Builder builder = new MaterialDialog.Builder(PullRequestDetailActivity.this).title(R.string.select_milestone)
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

    private void showCreateMilestone() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        builder.title(R.string.add_milestone);
        builder.content(R.string.add_milestone_description);
        builder.input(R.string.add_milestone_hint, 0, new MaterialDialog.InputCallback() {
            @Override
            public void onInput(MaterialDialog materialDialog, CharSequence milestoneName) {
                createMilestone(milestoneName.toString());
            }
        }).negativeText(R.string.cancel);

        builder.show();
    }

    private void createMilestone(String milestoneName) {
        CreateMilestoneRequestDTO createMilestoneRequestDTO = new CreateMilestoneRequestDTO(milestoneName);

        CreateMilestoneClient createMilestoneClient = new CreateMilestoneClient(issueInfo.repoInfo, createMilestoneRequestDTO);
        createMilestoneClient.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Milestone>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                hideProgressDialog();
            }

            @Override
            public void onNext(Milestone milestone) {
                addMilestone(milestone);
            }
        });
    }

    private void addMilestone(Milestone milestone) {
        showProgressDialog(R.string.adding_milestone);
        EditIssueMilestoneRequestDTO editIssueRequestDTO = new EditIssueMilestoneRequestDTO();
        editIssueRequestDTO.milestone = milestone.number;
        executeEditIssue(editIssueRequestDTO, R.string.issue_change_add_milestone);
    }

    private void clearMilestone() {
        showProgressDialog(R.string.clear_milestone);
        EditIssueMilestoneRequestDTO editIssueRequestDTO = new EditIssueMilestoneRequestDTO();
        editIssueRequestDTO.milestone = null;
        executeEditIssue(editIssueRequestDTO, R.string.issue_change_clear_milestone);
    }

    private void executeEditIssue(EditIssueRequestDTO editIssueRequestDTO, final int changedText) {
        EditIssueClient client = new EditIssueClient(issueInfo, editIssueRequestDTO);
        client.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Issue>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                ErrorHandler.onError(PullRequestDetailActivity.this, "Issue detail", e);
                hideProgressDialog();
            }

            @Override
            public void onNext(Issue issue) {
                shouldRefreshOnBack = true;
                hideProgressDialog();

                getContent();

                Snackbar.make(fab, changedText, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Labels
     */

    private void openLabels() {
        GithubIssueLabelsClient labelsClient = new GithubIssueLabelsClient(issueInfo.repoInfo, true);
        labelsClient.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new LabelsCallback());
    }

    private void setLabels(CharSequence[] selectedLabels) {
        if (selectedLabels != null) {

            if (selectedLabels.length > 0) {
                String[] labels = new String[selectedLabels.length];
                for (int i = 0; i < selectedLabels.length; i++) {
                    labels[i] = selectedLabels[i].toString();
                }
                EditIssueLabelsRequestDTO labelsRequestDTO = new EditIssueLabelsRequestDTO();
                labelsRequestDTO.labels = labels;
                executeEditIssue(labelsRequestDTO, R.string.issue_change_labels);
            } else {
                EditIssueLabelsRequestDTO labelsRequestDTO = new EditIssueLabelsRequestDTO();
                labelsRequestDTO.labels = new String[]{};
                executeEditIssue(labelsRequestDTO, R.string.issue_change_labels_clear);
            }
        } else {
            EditIssueLabelsRequestDTO labelsRequestDTO = new EditIssueLabelsRequestDTO();
            labelsRequestDTO.labels = new String[]{};
            executeEditIssue(labelsRequestDTO, R.string.issue_change_labels_clear);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(shouldRefreshOnBack ? RESULT_FIRST_USER : RESULT_OK);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == NEW_COMMENT_REQUEST) {
                showProgressDialog(R.string.adding_comment);
                final String body = data.getStringExtra(ContentEditorActivity.CONTENT);
                AddIssueCommentAction addIssueCommentAction = getAddIssueCommentAction(body);

                addIssueCommentAction.setAddCommentCallback(new CommentCallback(body));
                addIssueCommentAction.execute();
            } else if (requestCode == ISSUE_BODY_EDIT) {
                EditIssueBodyRequestDTO bodyRequestDTO = new EditIssueBodyRequestDTO();
                bodyRequestDTO.body = data.getStringExtra(ContentEditorActivity.CONTENT);

                executeEditIssue(bodyRequestDTO, R.string.issue_change_body);
            }
        }
    }

    @NonNull
    private AddIssueCommentAction getAddIssueCommentAction(String body) {
        return new AddIssueCommentAction(this, issueInfo, body, fab);
    }

    @Override
    public void onRefresh() {
        getContent();
        swipe.setOnRefreshListener(null);
    }

    private class LabelsCallback extends Subscriber<List<Label>> {

        private CharSequence[] selectedLabels;

        public void onNext(List<Label> labels) {
            if (labels != null) {
                List<String> items = new ArrayList<>();
                List<String> selectedLabels = new ArrayList<>();
                List<Integer> positionsSelectedLabels = new ArrayList<>();

                List<String> currentLabels = new ArrayList<>();
                for (Label label : pullRequestStory.pullRequest.labels) {
                    currentLabels.add(label.name);
                }

                int i = 0;
                for (Label label : labels) {
                    items.add(label.name);
                    if (currentLabels.contains(label.name)) {
                        selectedLabels.add(label.name);
                        positionsSelectedLabels.add(i);
                    }
                    i++;
                }

                LabelsCallback.this.selectedLabels = selectedLabels.toArray(new String[selectedLabels.size()]);

                MaterialDialog.Builder builder = new MaterialDialog.Builder(PullRequestDetailActivity.this);
                builder.items(items.toArray(new String[items.size()]));
                builder.alwaysCallMultiChoiceCallback();
                builder.itemsCallbackMultiChoice(positionsSelectedLabels.toArray(new Integer[positionsSelectedLabels.size()]),
                        new MaterialDialog.ListCallbackMultiChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {
                                LabelsCallback.this.selectedLabels = charSequences;
                                return true;
                            }
                        });
                builder.forceStacking(true);
                builder.positiveText(R.string.ok);
                //                builder.neutralText(R.string.add_new_label);
                builder.negativeText(R.string.clear_labels);
                builder.callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        setLabels(LabelsCallback.this.selectedLabels);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        LabelsCallback.this.selectedLabels = null;
                        setLabels(null);
                    }
                });
                builder.show();
            }
        }

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            ErrorHandler.onError(PullRequestDetailActivity.this, "Issue detail", e);
        }
    }

    private class CommentCallback implements AddIssueCommentAction.AddCommentCallback {
        private String body;

        private CommentCallback(String body) {
            this.body = body;
        }

        @Override
        public void onCommentAdded() {
            Snackbar.make(fab, R.string.add_comment_issue_fail, Snackbar.LENGTH_SHORT).setAction(R.string.retry, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddIssueCommentAction addIssueCommentAction1 = getAddIssueCommentAction(body);
                    addIssueCommentAction1.setAddCommentCallback(CommentCallback.this);
                    addIssueCommentAction1.execute();
                }
            }).show();
            getContent();
        }

        @Override
        public void onCommentError() {

        }
    }

    private class AssigneActionCallback implements ActionCallback<Boolean> {
        @Override
        public void onResult(Boolean result) {
            if (result) {
                Snackbar.make(fab, "Assignee changed", Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(fab, "Assignee change failed", Snackbar.LENGTH_SHORT).show();
            }
            getContent();
        }
    }
}

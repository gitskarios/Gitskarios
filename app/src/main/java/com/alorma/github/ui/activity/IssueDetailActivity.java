package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.StackingBehavior;
import com.alorma.github.GitskariosSettings;
import com.alorma.github.R;
import com.alorma.github.StoreCredentials;
import com.alorma.github.injector.component.ApiComponent;
import com.alorma.github.injector.component.ApplicationComponent;
import com.alorma.github.injector.component.DaggerApiComponent;
import com.alorma.github.injector.module.ApiModule;
import com.alorma.github.injector.module.issues.IssueDetailModule;
import com.alorma.github.presenter.issue.IssueCommentBaseRxPresenter;
import com.alorma.github.sdk.bean.dto.request.CreateMilestoneRequestDTO;
import com.alorma.github.sdk.bean.dto.request.EditIssueBodyRequestDTO;
import com.alorma.github.sdk.bean.dto.request.EditIssueLabelsRequestDTO;
import com.alorma.github.sdk.bean.dto.request.EditIssueMilestoneRequestDTO;
import com.alorma.github.sdk.bean.dto.request.EditIssueRequestDTO;
import com.alorma.github.sdk.bean.dto.request.EditIssueTitleRequestDTO;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.IssueState;
import com.alorma.github.sdk.bean.dto.response.Milestone;
import com.alorma.github.sdk.bean.dto.response.MilestoneState;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.bean.issue.IssueStory;
import com.alorma.github.sdk.bean.issue.IssueStoryComment;
import com.alorma.github.sdk.services.issues.CreateMilestoneClient;
import com.alorma.github.sdk.services.issues.EditIssueClient;
import com.alorma.github.sdk.services.issues.GetMilestonesClient;
import com.alorma.github.sdk.services.issues.GithubIssueLabelsClient;
import com.alorma.github.sdk.services.issues.story.IssueStoryLoader;
import com.alorma.github.sdk.services.repo.GetRepoClient;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.actions.ActionCallback;
import com.alorma.github.ui.actions.AddIssueCommentAction;
import com.alorma.github.ui.actions.ChangeAssigneeAction;
import com.alorma.github.ui.actions.CloseAction;
import com.alorma.github.ui.actions.ReopenAction;
import com.alorma.github.ui.actions.ShareAction;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.adapter.issues.IssueDetailAdapter;
import com.alorma.github.ui.listeners.IssueCommentRequestListener;
import com.alorma.github.ui.listeners.IssueDetailRequestListener;
import com.alorma.github.ui.utils.DialogUtils;
import com.alorma.github.utils.AttributesUtils;
import com.alorma.github.utils.ShortcutUtils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ValueAnimator;
import core.GithubComment;
import core.issue.EditIssueCommentBodyRequest;
import core.issues.Label;
import core.repositories.Repo;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class IssueDetailActivity extends BackActivity
    implements View.OnClickListener, IssueDetailRequestListener, SwipeRefreshLayout.OnRefreshListener, IssueCommentRequestListener,
    com.alorma.github.presenter.View<GithubComment> {

  @Inject IssueCommentBaseRxPresenter issueCommentPresenter;

  public static final String ISSUE_INFO_REPO_NAME = "ISSUE_INFO_REPO_NAME";
  public static final String ISSUE_INFO_REPO_OWNER = "ISSUE_INFO_REPO_OWNER";
  public static final String ISSUE_INFO_NUMBER = "ISSUE_INFO_NUMBER";

  private static final int NEW_COMMENT_REQUEST = 1243;
  private static final int ISSUE_BODY_EDIT = 4252;
  private static final int EDIT_COMMENT_REQUEST = 1121;

  public Repo repository;
  private boolean shouldRefreshOnBack;
  private IssueInfo issueInfo;

  @BindView(R.id.swipe) SwipeRefreshLayout swipe;
  @BindView(R.id.recycler) RecyclerView recyclerView;
  @BindView(R.id.addCommentEditText) EditText addCommentEditText;
  @BindView(R.id.addCommentButton) ImageView addCommentButton;
  @BindView(R.id.addCommentButtonExpand) ImageView addCommentButtonExpand;

  private IssueStory issueStory;
  private int primary;
  private int primaryDark;
  private ApiComponent apiComponent;

  public static Intent createLauncherIntent(Context context, IssueInfo issueInfo) {
    Bundle bundle = new Bundle();

    bundle.putString(ISSUE_INFO_REPO_NAME, issueInfo.repoInfo.name);
    bundle.putString(ISSUE_INFO_REPO_OWNER, issueInfo.repoInfo.owner);
    bundle.putInt(ISSUE_INFO_NUMBER, issueInfo.num);

    Intent intent = new Intent(context, IssueDetailActivity.class);
    intent.putExtras(bundle);
    return intent;
  }

  public static Intent createShortcutLauncherIntent(Context context, IssueInfo issueInfo) {
    Bundle bundle = new Bundle();

    bundle.putString(ISSUE_INFO_REPO_NAME, issueInfo.repoInfo.name);
    bundle.putString(ISSUE_INFO_REPO_OWNER, issueInfo.repoInfo.owner);
    bundle.putInt(ISSUE_INFO_NUMBER, issueInfo.num);

    Intent intent = new Intent(context, IssueDetailActivity.class);
    intent.putExtras(bundle);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.issue_detail_activity);
    issueCommentPresenter.attachView(this);

    if (getIntent().getExtras() != null) {

      String name = getIntent().getExtras().getString(ISSUE_INFO_REPO_NAME);
      String owner = getIntent().getExtras().getString(ISSUE_INFO_REPO_OWNER);

      RepoInfo repoInfo = new RepoInfo();
      repoInfo.name = name;
      repoInfo.owner = owner;

      int num = getIntent().getExtras().getInt(ISSUE_INFO_NUMBER);

      issueInfo = new IssueInfo();
      issueInfo.repoInfo = repoInfo;
      issueInfo.num = num;

      primary = AttributesUtils.getPrimaryColor(this);
      primaryDark = ContextCompat.getColor(this, R.color.primary_dark_alpha);

      initViews();

      getContent();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    issueCommentPresenter.detachView();
  }

  @Override
  protected void injectComponents(ApplicationComponent applicationComponent) {
    super.injectComponents(applicationComponent);

    apiComponent = DaggerApiComponent.builder().applicationComponent(applicationComponent).apiModule(new ApiModule()).build();
    apiComponent.plus(new IssueDetailModule()).inject(this);
  }

  private void checkEditTitle() {
    if (issueInfo != null && issueStory != null && issueStory.item != null) {

      StoreCredentials credentials = new StoreCredentials(this);

      GitskariosSettings settings = new GitskariosSettings(this);
      if (settings.shouldShowDialogEditIssue()) {
        if (issueInfo.repoInfo.permissions != null && issueInfo.repoInfo.permissions.push) {
          showEditDialog(R.string.dialog_edit_issue_edit_title_and_body_by_owner);
        } else if (issueStory.item.user.getLogin().equals(credentials.getUserName())) {
          showEditDialog(R.string.dialog_edit_issue_edit_title_and_body_by_author);
        }
      }
    }
  }

  private void showEditDialog(int content) {
    new DialogUtils().builder(this).title(R.string.dialog_edit_issue).content(content).positiveText(R.string.ok).show();
  }

  private void initViews() {
    ButterKnife.bind(this);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    swipe.setColorSchemeResources(R.color.accent_dark);
  }

  @Override
  protected void getContent() {
    super.getContent();

    hideProgressDialog();

    GetRepoClient repoClient = new GetRepoClient(issueInfo.repoInfo);
    repoClient.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(repo -> {
      issueInfo.repoInfo.permissions = repo.permissions;
      repository = repo;
      loadIssue();
    }, Throwable::printStackTrace);
  }

  private void loadIssue() {
    IssueStoryLoader issueStoryLoader = new IssueStoryLoader(issueInfo);
    issueStoryLoader.observable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::onResponseOk, Throwable::printStackTrace);
  }

  public void onResponseOk(IssueStory issueStory) {
    this.issueStory = issueStory;
    this.issueStory.item.repository = repository;

    swipe.setRefreshing(false);
    swipe.setOnRefreshListener(this);

    checkEditTitle();
    applyIssue();

    addCommentEditText.setEnabled(!issueStory.item.locked);
    addCommentButton.setEnabled(!issueStory.item.locked);

    if (!issueStory.item.locked) {
      addCommentEditText.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
          if (addCommentEditText.getText().toString().isEmpty()) {
            addCommentButton.setVisibility(View.GONE);
            addCommentButtonExpand.setVisibility(View.GONE);
            addCommentButton.setOnClickListener(null);
            addCommentButtonExpand.setOnClickListener(null);
          } else {
            addCommentButton.setVisibility(View.VISIBLE);
            addCommentButtonExpand.setVisibility(View.VISIBLE);
            addCommentButton.setOnClickListener(IssueDetailActivity.this);
            addCommentButtonExpand.setOnClickListener(IssueDetailActivity.this);
          }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
      });
    }
  }

  private void applyIssue() {
    changeColor(issueStory.item);

    String status = getString(R.string.issue_status_open);
    if (IssueState.closed == issueStory.item.state) {
      status = getString(R.string.issue_status_close);
    }
    setTitle("#" + issueStory.item.number + " " + status);
    IssueDetailAdapter adapter = new IssueDetailAdapter(this, getLayoutInflater(), issueStory, issueInfo.repoInfo, this, this);
    recyclerView.setAdapter(adapter);

    invalidateOptionsMenu();
  }

  private void changeColor(Issue issue) {
    int colorState = ContextCompat.getColor(this, R.color.issue_state_close);
    int colorStateDark = ContextCompat.getColor(this, R.color.issue_state_close_dark);
    if (IssueState.open == issue.state) {
      colorState = ContextCompat.getColor(this, R.color.issue_state_open);
      colorStateDark = ContextCompat.getColor(this, R.color.issue_state_open_dark);
    }

    View toolbarBg = findViewById(R.id.toolbarBg);
    if (toolbarBg != null) {
      toolbarBg.setBackgroundColor(colorState);
    }

    swipe.setColorSchemeColors(colorState);

    ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), primary, colorState);
    colorAnimation.addUpdateListener(animator -> {
      int color = (Integer) animator.getAnimatedValue();
      if (getToolbar() != null) {
        getToolbar().setBackgroundColor(color);
      }
    });

    ValueAnimator colorAnimationStatus = ValueAnimator.ofObject(new ArgbEvaluator(), primaryDark, colorStateDark);
    colorAnimationStatus.addUpdateListener(animator -> {
      int color = (Integer) animator.getAnimatedValue();

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        getWindow().setStatusBarColor(color);
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

  public void showError() {
    hideProgressDialog();
    MaterialDialog.Builder builder = new DialogUtils().builder(this);
    builder.title(R.string.ups);
    builder.content(getString(R.string.issue_detail_error, issueInfo.toString()));
    builder.positiveText(R.string.retry);
    builder.negativeText(R.string.accept);
    builder.onPositive((dialog1, which) -> getContent());
    builder.onNegative((dialog1, which) -> finish());
    dialog = builder.show();
  }

  @Override
  public void onClick(View view) {
    if (view.getId() == R.id.addCommentButton) {
      addComment(addCommentEditText.getText().toString());
      addCommentEditText.setText("");
    } else {
      String hint = getString(R.string.add_comment);
      Intent intent = ContentEditorActivity.createLauncherIntent(this, issueInfo.repoInfo, issueInfo.num, hint, null, false, false);
      startActivityForResult(intent, NEW_COMMENT_REQUEST);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    if (this.issueStory != null) {

      if (issueInfo.repoInfo.permissions != null && issueInfo.repoInfo.permissions.push) {
        getMenuInflater().inflate(R.menu.issue_detail, menu);
      } else {
        getMenuInflater().inflate(R.menu.issue_detail_no_permissions, menu);
      }

      MenuItem item = menu.findItem(R.id.share_issue);

      item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_menu_share_mtrl_alpha));
    }
    return true;
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    if (this.issueStory != null) {

      if (issueInfo.repoInfo.permissions != null && issueInfo.repoInfo.permissions.push || (accountNameProvider.getName() != null
          && accountNameProvider.getName().equals(issueStory.item.user.getLogin()))) {
        if (menu.findItem(R.id.action_close_issue) != null) {
          menu.removeItem(R.id.action_close_issue);
        }
        if (menu.findItem(R.id.action_reopen_issue) != null) {
          menu.removeItem(R.id.action_reopen_issue);
        }
        if (issueStory.item.state == IssueState.closed) {
          MenuItem menuItem = menu.add(0, R.id.action_reopen_issue, 1, getString(R.string.reopenIssue));
          menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        } else {

          MenuItem menuItem = menu.add(0, R.id.action_close_issue, 1, getString(R.string.closeIssue));
          menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        }
      }
    }

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
        new CloseAction(this, issueInfo, R.string.closeIssue).setCallback(issue -> {
          getContent();
          Snackbar.make(recyclerView, R.string.issue_change, Snackbar.LENGTH_SHORT).show();
        }).execute();
        break;
      case R.id.action_reopen_issue:
        new ReopenAction(this, issueInfo, R.string.reopenIssue).setCallback(issue -> {
          getContent();
          Snackbar.make(recyclerView, R.string.issue_change, Snackbar.LENGTH_SHORT).show();
        }).execute();
        break;
      case R.id.issue_edit_milestone:
        editMilestone();
        break;
      case R.id.issue_edit_assignee:
        new ChangeAssigneeAction(this, apiComponent, issueStory.item.assignees, issueInfo).setCallback(new AssigneActionCallback())
            .execute();
        break;
      case R.id.issue_edit_labels:
        openLabels();
        break;
      case R.id.share_issue:
        if (issueStory != null && issueStory.item != null) {

          String title = issueInfo.toString();
          String url = issueStory.item.getHtmlUrl();

          new ShareAction(this, title, url).setType("Issue").execute();
        }
        break;
      case R.id.action_add_shortcut:
        ShortcutUtils.addShortcut(this, issueInfo);
        break;
    }

    return true;
  }

  private void editMilestone() {
    GetMilestonesClient milestonesClient = new GetMilestonesClient(issueInfo.repoInfo, MilestoneState.open, true);
    milestonesClient.observable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new MilestonesCallback());

    showProgressDialog(R.string.loading_milestones);
  }

  @Override
  public void onTitleEditRequest() {
    dialog = new DialogUtils().builder(this)
        .title(R.string.edit_issue_title)
        .input(null, issueStory.item.title, false, new MaterialDialog.InputCallback() {
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
    String body = issueStory.item.body != null ? issueStory.item.body.replace("\n", "<br />") : "";
    Intent launcherIntent =
        ContentEditorActivity.createLauncherIntent(this, issueInfo.repoInfo, issueInfo.num, getString(R.string.edit_issue_body_hint), body,
            true, false);
    startActivityForResult(launcherIntent, ISSUE_BODY_EDIT);
  }

  private void showCreateMilestone() {
    MaterialDialog.Builder builder = new DialogUtils().builder(this);
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
    createMilestoneClient.observable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<Milestone>() {
          @Override
          public void onCompleted() {

          }

          @Override
          public void onError(Throwable e) {

          }

          @Override
          public void onNext(Milestone milestone) {
            addMilestone(milestone);
          }
        });
  }

  private void addMilestone(Milestone milestone) {
    showProgressDialog(R.string.add_milestone);
    EditIssueMilestoneRequestDTO editIssueRequestDTO = new EditIssueMilestoneRequestDTO();
    editIssueRequestDTO.milestone = milestone.number;
    executeEditIssue(editIssueRequestDTO, R.string.issue_change_add_milestone);
  }

  private void addComment(String body) {
    showProgressDialog(R.string.adding_comment);
    AddIssueCommentAction addIssueCommentAction = getAddIssueCommentAction(body);

    addIssueCommentAction.setAddCommentCallback(new CommentCallback(body));
    addIssueCommentAction.execute();
  }

  private void clearMilestone() {
    showProgressDialog(R.string.clear_milestone);
    EditIssueMilestoneRequestDTO editIssueRequestDTO = new EditIssueMilestoneRequestDTO();
    editIssueRequestDTO.milestone = null;
    executeEditIssue(editIssueRequestDTO, R.string.issue_change_clear_milestone);
  }

  private void executeEditIssue(final EditIssueRequestDTO editIssueRequestDTO, final int changedText) {
    EditIssueClient client = new EditIssueClient(issueInfo, editIssueRequestDTO);

    client.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Issue>() {
      @Override
      public void onCompleted() {

      }

      @Override
      public void onError(Throwable e) {

      }

      @Override
      public void onNext(Issue issue) {
        shouldRefreshOnBack = true;
        hideProgressDialog();
        getContent();

        Snackbar.make(recyclerView, changedText, Snackbar.LENGTH_SHORT).show();
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
        labelsRequestDTO.labels = new String[] {};
        executeEditIssue(labelsRequestDTO, R.string.issue_change_labels_clear);
      }
    } else {
      EditIssueLabelsRequestDTO labelsRequestDTO = new EditIssueLabelsRequestDTO();
      labelsRequestDTO.labels = new String[] {};
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
        String body = data.getStringExtra(ContentEditorActivity.CONTENT);
        addComment(body);
      } else if (requestCode == ISSUE_BODY_EDIT) {
        EditIssueBodyRequestDTO bodyRequestDTO = new EditIssueBodyRequestDTO();
        bodyRequestDTO.body = data.getStringExtra(ContentEditorActivity.CONTENT);

        executeEditIssue(bodyRequestDTO, R.string.issue_change_body);
      } else if (requestCode == EDIT_COMMENT_REQUEST) {
        IssueStoryComment comment = data.getParcelableExtra(ContentEditorActivity.PARCELABLE);
        String body = data.getStringExtra(ContentEditorActivity.CONTENT);

        editComment(comment, body);
      }
    }
  }

  @NonNull
  private AddIssueCommentAction getAddIssueCommentAction(String body) {
    return new AddIssueCommentAction(issueInfo, body);
  }

  @Override
  public void onRefresh() {
    getContent();
    swipe.setOnRefreshListener(null);
  }

  @Override
  public void onContentEditRequest(IssueStoryComment issueStoryComment) {
    Intent intent = ContentEditorActivity.createLauncherIntent(this, null, issueStoryComment.comment.body, true, false);
    intent.putExtra(ContentEditorActivity.PARCELABLE, issueStoryComment);
    startActivityForResult(intent, EDIT_COMMENT_REQUEST);
  }

  private void editComment(IssueStoryComment issueStoryComment, String string) {
    RepoInfo repoInfo = new RepoInfo();
    repoInfo.name = issueInfo.repoInfo.name;
    repoInfo.owner = issueInfo.repoInfo.owner;
    EditIssueCommentBodyRequest edit = new EditIssueCommentBodyRequest(repoInfo, issueStoryComment.comment.id, string);
    issueCommentPresenter.execute(edit);
  }

  @Override
  public void showLoading() {
  }

  @Override
  public void hideLoading() {
  }

  @Override
  public void onDataReceived(GithubComment data, boolean isFromPaginated) {
    getContent();
  }

  @Override
  public void showError(Throwable throwable) {

  }

  private class MilestonesCallback implements Observer<List<Milestone>> {
    @Override
    public void onNext(final List<Milestone> milestones) {
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
          if (IssueDetailActivity.this.issueStory.item.milestone != null) {
            String currentMilestone = IssueDetailActivity.this.issueStory.item.milestone.title;
            if (currentMilestone != null && currentMilestone.equals(milestones.get(i).title)) {
              selectedMilestone = i;
              break;
            }
          }
        }

        MaterialDialog.Builder builder = new DialogUtils().builder(IssueDetailActivity.this)
            .title(R.string.select_milestone)
            .items(itemsMilestones)
            .itemsCallbackSingleChoice(selectedMilestone, (materialDialog, view, i, text) -> {

              addMilestone(milestones.get(i));

              return false;
            })
            .forceStacking(true)
            .widgetColor(AttributesUtils.getPrimaryColor(IssueDetailActivity.this))
            .negativeText(R.string.add_milestone);

        if (selectedMilestone != -1) {
          builder.neutralText(R.string.clear_milestone);
        }

        builder.onNegative((dialog1, which) -> clearMilestone());
        builder.onPositive((dialog1, which) -> showCreateMilestone());

        builder.show();
      }
    }

    @Override
    public void onError(Throwable error) {

    }

    @Override
    public void onCompleted() {

    }
  }

  private class LabelsCallback implements Observer<List<Label>> {

    private CharSequence[] selectedLabels;

    @Override
    public void onNext(List<Label> labels) {
      if (labels != null) {
        List<String> items = new ArrayList<>();
        List<String> selectedLabels = new ArrayList<>();
        List<Integer> positionsSelectedLabels = new ArrayList<>();

        List<String> currentLabels = new ArrayList<>();
        for (Label label : issueStory.item.labels) {
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

        MaterialDialog.Builder builder = new DialogUtils().builder(IssueDetailActivity.this);
        builder.items(items);
        builder.alwaysCallMultiChoiceCallback();
        builder.itemsCallbackMultiChoice(positionsSelectedLabels.toArray(new Integer[positionsSelectedLabels.size()]),
            (materialDialog, integers, charSequences) -> {
              LabelsCallback.this.selectedLabels = charSequences;
              return true;
            });
        builder.stackingBehavior(StackingBehavior.ADAPTIVE);
        builder.positiveText(R.string.ok);
        builder.negativeText(R.string.clear_labels);
        builder.onPositive((dialog1, which) -> setLabels(LabelsCallback.this.selectedLabels));
        builder.onNegative((dialog1, which) -> {
          LabelsCallback.this.selectedLabels = null;
          setLabels(null);
        });
        dialog = builder.show();
      }
    }

    @Override
    public void onError(Throwable error) {
      ErrorHandler.onError(IssueDetailActivity.this, "Issue detail", error);
    }

    @Override
    public void onCompleted() {

    }
  }

  private class CommentCallback implements AddIssueCommentAction.AddCommentCallback {
    private String body;

    private CommentCallback(String body) {
      this.body = body;
    }

    @Override
    public void onCommentAdded() {
      Snackbar.make(recyclerView, R.string.add_comment_issue_ok, Snackbar.LENGTH_SHORT).show();
      addCommentEditText.setText("");

      getContent();
    }

    @Override
    public void onCommentError() {
      if (dialog != null) {
        dialog.dismiss();
      }
      Snackbar.make(recyclerView, R.string.add_comment_issue_fail, Snackbar.LENGTH_SHORT).setAction(R.string.retry, v -> {
        addComment(body);
      }).show();
    }

    @Override
    public void onCommentAddStarted() {

    }
  }

  private class AssigneActionCallback implements ActionCallback<Boolean> {
    @Override
    public void onResult(Boolean result) {
      if (result) {
        Snackbar.make(recyclerView, "Assignee changed", Snackbar.LENGTH_SHORT).show();
      } else {
        Snackbar.make(recyclerView, "Assignee change failed", Snackbar.LENGTH_SHORT).show();
      }
      getContent();
    }
  }
}

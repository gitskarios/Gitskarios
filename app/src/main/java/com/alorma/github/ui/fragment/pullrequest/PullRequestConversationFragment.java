package com.alorma.github.ui.fragment.pullrequest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alorma.github.GitskariosSettings;
import com.alorma.github.R;
import com.alorma.github.StoreCredentials;
import com.alorma.github.sdk.bean.dto.request.EditIssueBodyRequestDTO;
import com.alorma.github.sdk.bean.dto.request.EditIssueRequestDTO;
import com.alorma.github.sdk.bean.dto.request.EditIssueTitleRequestDTO;
import com.alorma.github.sdk.bean.dto.request.MergeButtonRequest;
import com.alorma.github.sdk.bean.dto.request.UpdateReferenceRequest;
import com.alorma.github.sdk.bean.dto.response.GitReference;
import com.alorma.github.sdk.bean.dto.response.Head;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.IssueState;
import com.alorma.github.sdk.bean.dto.response.MergeButtonResponse;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.bean.issue.IssueStoryComment;
import com.alorma.github.sdk.bean.issue.PullRequestStory;
import com.alorma.github.sdk.services.issues.EditIssueClient;
import com.alorma.github.sdk.services.pullrequest.MergePullRequestClient;
import com.alorma.github.sdk.services.pullrequest.story.PullRequestStoryLoader;
import com.alorma.github.sdk.services.reference.DeleteReferenceClient;
import com.alorma.github.sdk.services.reference.GetReferenceClient;
import com.alorma.github.sdk.services.repo.CreateRepositoryClient;
import com.alorma.github.sdk.services.repo.GetRepoClient;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.actions.AddIssueCommentAction;
import com.alorma.github.ui.activity.ContentEditorActivity;
import com.alorma.github.ui.adapter.issues.PullRequestDetailAdapter;
import com.alorma.github.ui.fragment.base.BaseFragment;
import com.alorma.github.ui.listeners.IssueCommentRequestListener;
import com.alorma.github.ui.listeners.IssueDetailRequestListener;
import com.alorma.github.ui.utils.DialogUtils;
import com.alorma.github.ui.view.pullrequest.PullRequestDetailView;
import com.alorma.github.utils.IssueUtils;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PullRequestConversationFragment extends BaseFragment
    implements PullRequestDetailView.PullRequestActionsListener, IssueDetailRequestListener, SwipeRefreshLayout.OnRefreshListener,
    IssueCommentRequestListener {

  public static final String ISSUE_INFO = "ISSUE_INFO";
  public static final String ISSUE_INFO_REPO_NAME = "ISSUE_INFO_REPO_NAME";
  public static final String ISSUE_INFO_REPO_OWNER = "ISSUE_INFO_REPO_OWNER";
  public static final String ISSUE_INFO_NUMBER = "ISSUE_INFO_NUMBER";

  private static final int NEW_COMMENT_REQUEST = 1243;
  private static final int ISSUE_BODY_EDIT = 4252;

  private IssueInfo issueInfo;

  private SwipeRefreshLayout swipe;
  private RecyclerView recyclerView;

  private PullRequestStory pullRequestStory;
  private Repo repository;

  private PullRequestStoryLoaderInterface pullRequestStoryLoaderInterfaceNull = story -> {
  };
  private PullRequestStoryLoaderInterface pullRequestStoryLoaderInterface = pullRequestStoryLoaderInterfaceNull;
  private PullRequestDetailAdapter adapter;
  private boolean headReferenceExist = false;
  private boolean hasPushPermissionsToHead = false;

  public static PullRequestConversationFragment newInstance(IssueInfo issueInfo) {
    Bundle bundle = new Bundle();

    bundle.putParcelable(ISSUE_INFO, issueInfo);

    PullRequestConversationFragment fragment = new PullRequestConversationFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.pullrequest_detail_fragment, null, false);
  }

  @Override
  protected int getLightTheme() {
    return R.style.AppTheme_Repository;
  }

  @Override
  protected int getDarkTheme() {
    return R.style.AppTheme_Dark_Repository;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    findViews(view);

    if (getArguments() != null) {
      issueInfo = getArguments().getParcelable(ISSUE_INFO);

      if (issueInfo == null && getArguments().containsKey(ISSUE_INFO_NUMBER)) {
        String name = getArguments().getString(ISSUE_INFO_REPO_NAME);
        String owner = getArguments().getString(ISSUE_INFO_REPO_OWNER);

        RepoInfo repoInfo = new RepoInfo();
        repoInfo.name = name;
        repoInfo.owner = owner;

        int num = getArguments().getInt(ISSUE_INFO_NUMBER);

        issueInfo = new IssueInfo();
        issueInfo.repoInfo = repoInfo;
        issueInfo.num = num;
      }

      setHasOptionsMenu(true);
      getContent();
    }
  }

  @Override
  public void onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);

    if (pullRequestStory != null && new IssueUtils().canComment(pullRequestStory.item)) {
      getActivity().getMenuInflater().inflate(R.menu.pullrequest_detail_overview, menu);

      MenuItem item = menu.findItem(R.id.action_pull_request_add_comment);
      if (item != null) {
        item.setIcon(new IconicsDrawable(getActivity()).icon(Octicons.Icon.oct_comment_add).actionBar().color(Color.WHITE));
      }
    } else {
      menu.removeItem(R.id.action_pull_request_add_comment);
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_pull_request_add_comment) {
      onAddComment();
    }

    return super.onOptionsItemSelected(item);
  }

  private void onAddComment() {
    String hint = getString(R.string.add_comment);
    Intent intent = ContentEditorActivity.createLauncherIntent(getActivity(), issueInfo.repoInfo, issueInfo.num, hint, null, false, false);
    startActivityForResult(intent, NEW_COMMENT_REQUEST);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == Activity.RESULT_OK && data != null) {
      if (requestCode == NEW_COMMENT_REQUEST) {
        final String body = data.getStringExtra(ContentEditorActivity.CONTENT);
        AddIssueCommentAction addIssueCommentAction = getAddIssueCommentAction(body);
        addIssueCommentAction.setAddCommentCallback(new CommentCallback());
        addIssueCommentAction.execute();
      } else if (requestCode == ISSUE_BODY_EDIT) {
        EditIssueBodyRequestDTO bodyRequestDTO = new EditIssueBodyRequestDTO();
        bodyRequestDTO.body = data.getStringExtra(ContentEditorActivity.CONTENT);

        executeEditIssue(bodyRequestDTO);
      }
    }
  }

  public void setPullRequestStoryLoaderInterface(PullRequestStoryLoaderInterface pullRequestStoryLoaderInterface) {
    this.pullRequestStoryLoaderInterface = pullRequestStoryLoaderInterface;
  }

  @NonNull
  private AddIssueCommentAction getAddIssueCommentAction(String body) {
    return new AddIssueCommentAction(issueInfo, body);
  }

  private void checkEditTitle() {
    if (getActivity() != null) {
      if (issueInfo != null && pullRequestStoryItemExist()) {

        StoreCredentials credentials = new StoreCredentials(getActivity());

        GitskariosSettings settings = new GitskariosSettings(getActivity());
        if (settings.shouldShowDialogEditIssue()) {
          if (issueInfo.repoInfo.permissions != null && issueInfo.repoInfo.permissions.push) {
            showEditDialog(R.string.dialog_edit_issue_edit_title_and_body_by_owner);
          } else if (pullRequestStory.item.user.login.equals(credentials.getUserName())) {
            showEditDialog(R.string.dialog_edit_issue_edit_title_and_body_by_author);
          }
        }
      }
    }
  }

  private void showEditDialog(int content) {
    new DialogUtils().builder(getActivity()).title(R.string.dialog_edit_issue).content(content).positiveText(R.string.ok).show();
  }

  private void findViews(View rootView) {
    recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    swipe = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe);
    if (swipe != null) {
      swipe.setColorSchemeResources(R.color.accent);
    }
  }

  private void getContent() {
    if (pullRequestStory == null) {
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
      onResponseOk(pullRequestStory);
    }
  }

  private void loadPullRequest() {
    PullRequestStoryLoader pullRequestStoryLoader = new PullRequestStoryLoader(issueInfo);
    pullRequestStoryLoader.observable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<PullRequestStory>() {
          @Override
          public void onCompleted() {

          }

          @Override
          public void onError(Throwable e) {
            showError();
          }

          @Override
          public void onNext(PullRequestStory pullRequestStory) {
            checkHeadBranchExist(pullRequestStory);
            onResponseOk(pullRequestStory);
          }
        });
  }

  private void showError() {
    MaterialDialog.Builder builder = new DialogUtils().builder(getActivity());
    builder.title(R.string.ups);
    builder.content(getString(R.string.issue_detail_error, issueInfo.toString()));
    builder.positiveText(R.string.retry);
    builder.negativeText(R.string.accept);
    builder.onPositive((dialog, which) -> getContent());
    builder.onNegative((dialog, which) -> getActivity().finish());
    builder.show();
  }

  public void onResponseOk(final PullRequestStory pullRequestStory) {
    if (getActivity() != null) {
      getActivity().invalidateOptionsMenu();
      this.pullRequestStory = pullRequestStory;
      this.pullRequestStory.item.repository = repository;

      if (pullRequestStoryLoaderInterface != null) {
        pullRequestStoryLoaderInterface.onStoryLoaded(pullRequestStory);
      }

      swipe.setRefreshing(false);
      swipe.setOnRefreshListener(this);

      applyIssue();
    }
  }

  private void checkHeadBranchExist(PullRequestStory pullRequestStory) {

    RepoInfo headRepoInfo = pullRequestStory.item.head.repo.toInfo();
    GetRepoClient getRepoClient = new GetRepoClient(headRepoInfo);
    GetReferenceClient referenceClient =
            new GetReferenceClient(headRepoInfo, pullRequestStory.item.head.ref);
    getRepoClient
            .observable()
            .flatMap((repo) -> {
              if (repo.permissions != null) {
                hasPushPermissionsToHead = repo.permissions.push;
              }

              return referenceClient.observable()
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread());
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Subscriber<GitReference>() {
              @Override
              public void onCompleted() {
              }

              @Override
              public void onError(Throwable e) {
                notifyHeadOfAdapter(false);
              }

              @Override
              public void onNext(GitReference reference) {
                notifyHeadOfAdapter(true);
              }
            });
  }

  private void notifyHeadOfAdapter(boolean headReferenceExist) {
    // refresh head only if pull request is closed or merged
    if (pullRequestStoryItemExist() && pullRequestClosedOrMerged()) {
      this.headReferenceExist = headReferenceExist;
      adapter.notifyItemChanged(0);
    }
  }

  private boolean pullRequestClosedOrMerged() {
    return pullRequestStory.item.state == IssueState.closed
    || pullRequestStory.item.merged;
  }

  private boolean pullRequestStoryItemExist() {
    return pullRequestStory != null && pullRequestStory.item != null;
  }

  private void applyIssue() {
    checkEditTitle();
    // TODO changeColor(pullRequestStory.item);

    String status = getString(R.string.issue_status_open);
    if (IssueState.closed == pullRequestStory.item.state) {
      status = getString(R.string.issue_status_close);
    } else if (pullRequestStory.item.merged) {
      status = getString(R.string.pullrequest_status_merged);
    }
    getActivity().setTitle("#" + pullRequestStory.item.number + " " + status);
    adapter =
        new PullRequestDetailAdapter(getActivity(), recyclerView, getActivity().getLayoutInflater(), pullRequestStory, issueInfo.repoInfo, this, this);
    recyclerView.setAdapter(adapter);

    getActivity().invalidateOptionsMenu();
  }

  @Override
  public void onRefresh() {
    getContent();
    swipe.setOnRefreshListener(null);
  }

  @Override
  public void onTitleEditRequest() {
    new DialogUtils().builder(getActivity())
        .title(R.string.edit_issue_title)
        .input(null, pullRequestStory.item.title, false, (materialDialog, charSequence) -> {

          EditIssueTitleRequestDTO editIssueTitleRequestDTO = new EditIssueTitleRequestDTO();
          editIssueTitleRequestDTO.title = charSequence.toString();
          executeEditIssue(editIssueTitleRequestDTO);
        })
        .positiveText(R.string.edit_issue_button_ok)
        .neutralText(R.string.edit_issue_button_neutral)
        .show();
  }

  @Override
  public void onContentEditRequest() {
    String body = pullRequestStory.item.body != null ? pullRequestStory.item.body.replace("\n", "<br />") : "";
    Intent launcherIntent = ContentEditorActivity.createLauncherIntent(getActivity(), issueInfo.repoInfo, issueInfo.num,
        getString(R.string.edit_issue_body_hint), body, true, false);
    startActivityForResult(launcherIntent, ISSUE_BODY_EDIT);
  }

  private void executeEditIssue(EditIssueRequestDTO editIssueRequestDTO) {
    EditIssueClient client = new EditIssueClient(issueInfo, editIssueRequestDTO);
    client.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Issue>() {
      @Override
      public void onCompleted() {

      }

      @Override
      public void onError(Throwable e) {
        ErrorHandler.onError(getActivity(), "Issue detail", e);
      }

      @Override
      public void onNext(Issue issue) {
        getContent();
      }
    });
  }

  @Override
  public void mergeRequest(Head head, Head base) {
    MaterialDialog.Builder builder = new DialogUtils().builder(getActivity());
    builder.title(R.string.merge_title);
    builder.content(head.label);
    builder.input(getString(R.string.merge_message), pullRequestStory.item.title, false, (materialDialog, charSequence) -> {
      merge(charSequence.toString(), head.sha, issueInfo);
    });
    builder.inputType(InputType.TYPE_CLASS_TEXT);
    dialog = builder.show();
  }

  @Override
  public boolean userIsAbleToDelete() {
    return headReferenceExist && hasPushPermissionsToHead;
  }

  @Override
  public void deleteHeadReference(Head head) {
    MaterialDialog.Builder builder = new DialogUtils().builder(getActivity());
    builder.title(R.string.pull_request_delete_branch_question);
    builder.content(head.ref);
    builder.positiveText(R.string.ok);
    builder.negativeText(R.string.cancel);
    builder.onPositive((dialog1, which) -> {
      callDeleteHeadReference(head);
    });
    builder.onNegative((dialog1, which) -> {
      dialog1.dismiss();
    });
    dialog = builder.show();
  }

  private void callDeleteHeadReference(Head head) {
    UpdateReferenceRequest updateReferenceRequest = new UpdateReferenceRequest();
    updateReferenceRequest.sha = head.sha;
    updateReferenceRequest.force = true;
    String ref = head.ref;

    DeleteReferenceClient deleteReferenceClient =
            new DeleteReferenceClient(repository.toInfo(), ref);
    deleteReferenceClient.observable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe((result) -> {
                      if (result) {
                        restartActivity();
                      } else {
                        Toast.makeText(getActivity(), "Failed to delete branch.", Toast.LENGTH_SHORT).show();
                      }
                    }, (throwable) -> {
                      Toast.makeText(getActivity(), "Failed to delete branch.", Toast.LENGTH_SHORT).show();
                    }
            );
  }

  private void merge(String message, String sha, IssueInfo issueInfo) {
    MergeButtonRequest mergeButtonRequest = new MergeButtonRequest();
    mergeButtonRequest.commit_message = message;
    mergeButtonRequest.sha = sha;
    MergePullRequestClient mergePullRequestClient = new MergePullRequestClient(issueInfo, mergeButtonRequest);
    mergePullRequestClient.observable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<MergeButtonResponse>() {
          @Override
          public void onCompleted() {

          }

          @Override
          public void onError(Throwable e) {

          }

          @Override
          public void onNext(MergeButtonResponse mergeButtonResponse) {
            restartActivity();
          }
        });
  }

  private void restartActivity() {
    startActivity(getActivity().getIntent());
    getActivity().finish();
  }

  @Override
  public void onContentEditRequest(IssueStoryComment issueStoryComment) {
    if (getActivity() != null) {
      Toast.makeText(getActivity(), "Not ready yet", Toast.LENGTH_SHORT).show();
    }
  }

  public interface PullRequestStoryLoaderInterface {
    void onStoryLoaded(PullRequestStory story);
  }

  private class CommentCallback implements AddIssueCommentAction.AddCommentCallback {

    private ProgressDialog progressDialog;

    private CommentCallback() {
    }

    @Override
    public void onCommentAdded() {
      if (progressDialog != null) {
        progressDialog.dismiss();
      }
      pullRequestStory = null;
      getContent();
    }

    @Override
    public void onCommentError() {
      if (progressDialog != null) {
        progressDialog.dismiss();
      }
    }

    @Override
    public void onCommentAddStarted() {
      progressDialog = new ProgressDialog(getActivity());
      progressDialog.setMessage(getString(R.string.adding_comment));
      progressDialog.setCancelable(true);
      progressDialog.show();
    }
  }
}

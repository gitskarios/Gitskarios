package com.alorma.github.ui.fragment.pullrequest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alorma.github.GitskariosSettings;
import com.alorma.github.R;
import com.alorma.github.StoreCredentials;
import com.alorma.github.sdk.bean.dto.request.EditIssueBodyRequestDTO;
import com.alorma.github.sdk.bean.dto.request.EditIssueRequestDTO;
import com.alorma.github.sdk.bean.dto.request.EditIssueTitleRequestDTO;
import com.alorma.github.sdk.bean.dto.request.MergeButtonRequest;
import com.alorma.github.sdk.bean.dto.response.GithubStatusResponse;
import com.alorma.github.sdk.bean.dto.response.Head;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.IssueState;
import com.alorma.github.sdk.bean.dto.response.MergeButtonResponse;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.bean.issue.PullRequestStory;
import com.alorma.github.sdk.services.issues.EditIssueClient;
import com.alorma.github.sdk.services.pullrequest.MergePullRequestClient;
import com.alorma.github.sdk.services.pullrequest.story.PullRequestStoryLoader;
import com.alorma.github.sdk.services.repo.GetRepoClient;
import com.alorma.github.sdk.services.repo.GetShaCombinedStatus;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.actions.AddIssueCommentAction;
import com.alorma.github.ui.activity.ContentEditorActivity;
import com.alorma.github.ui.adapter.issues.PullRequestDetailAdapter;
import com.alorma.github.ui.listeners.IssueDetailRequestListener;
import com.alorma.github.ui.view.pullrequest.PullRequestDetailView;
import com.alorma.gitskarios.core.Pair;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class PullRequestDetailOverviewFragment extends Fragment
    implements PullRequestDetailView.PullRequestActionsListener, IssueDetailRequestListener,
    SwipeRefreshLayout.OnRefreshListener {

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

  private PullRequestDetailAdapter adapter;

  public static PullRequestDetailOverviewFragment newInstance(IssueInfo issueInfo) {
    Bundle bundle = new Bundle();

    bundle.putParcelable(ISSUE_INFO, issueInfo);

    PullRequestDetailOverviewFragment fragment = new PullRequestDetailOverviewFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
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
    }
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.pullrequest_detail_fragment, null, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    findViews(view);
    getContent();
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);

    inflater.inflate(R.menu.pullrequest_detail_overview, menu);

    MenuItem item = menu.findItem(R.id.action_pull_request_add_comment);

    if (item != null) {
      item.setIcon(new IconicsDrawable(getActivity()).icon(Octicons.Icon.oct_comment_add)
          .actionBar()
          .color(Color.WHITE));
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
    Intent intent =
        ContentEditorActivity.createLauncherIntent(getActivity(), issueInfo.repoInfo, issueInfo.num,
            hint, null, false, false);
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
      }
      if (requestCode == ISSUE_BODY_EDIT) {
        EditIssueBodyRequestDTO bodyRequestDTO = new EditIssueBodyRequestDTO();
        bodyRequestDTO.body = data.getStringExtra(ContentEditorActivity.CONTENT);

        executeEditIssue(bodyRequestDTO);
      }
    }
  }

  private class CommentCallback implements AddIssueCommentAction.AddCommentCallback {

    private CommentCallback() {
    }

    @Override
    public void onCommentAdded() {
      getContent();
    }

    @Override
    public void onCommentError() {

    }
  }

  @NonNull
  private AddIssueCommentAction getAddIssueCommentAction(String body) {
    return new AddIssueCommentAction(issueInfo, body);
  }

  private void checkEditTitle() {
    if (getActivity() != null) {
      if (issueInfo != null && pullRequestStory != null && pullRequestStory.pullRequest != null) {

        StoreCredentials credentials = new StoreCredentials(getActivity());

        GitskariosSettings settings = new GitskariosSettings(getActivity());
        if (settings.shouldShowDialogEditIssue()) {
          if (issueInfo.repoInfo.permissions != null && issueInfo.repoInfo.permissions.push) {
            showEditDialog(R.string.dialog_edit_issue_edit_title_and_body_by_owner);
          } else if (pullRequestStory.pullRequest.user.login.equals(credentials.getUserName())) {
            showEditDialog(R.string.dialog_edit_issue_edit_title_and_body_by_author);
          }
        }
      }
    }
  }

  private void showEditDialog(int content) {
    new MaterialDialog.Builder(getActivity()).title(R.string.dialog_edit_issue)
        .content(content)
        .positiveText(R.string.ok)
        .show();
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
    GetRepoClient repoClient = new GetRepoClient(issueInfo.repoInfo);
    repoClient.observable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Repo>() {
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
            onResponseOk(pullRequestStory);
          }
        });
  }

  private void showError() {
    MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
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
      this.pullRequestStory = pullRequestStory;
      this.pullRequestStory.pullRequest.repository = repository;

      swipe.setRefreshing(false);
      swipe.setOnRefreshListener(this);

      GetShaCombinedStatus status =
          new GetShaCombinedStatus(issueInfo.repoInfo, pullRequestStory.pullRequest.head.ref);
      status.observable()
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .map(new Func1<Pair<GithubStatusResponse, Integer>, GithubStatusResponse>() {
            @Override
            public GithubStatusResponse call(
                Pair<GithubStatusResponse, Integer> githubStatusResponseIntegerPair) {
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
  }

  private void applyIssue() {
    checkEditTitle();
    // TODO changeColor(pullRequestStory.pullRequest);

    String status = getString(R.string.issue_status_open);
    if (IssueState.closed == pullRequestStory.pullRequest.state) {
      status = getString(R.string.issue_status_close);
    } else if (pullRequestStory.pullRequest.merged) {
      status = getString(R.string.pullrequest_status_merged);
    }
    getActivity().setTitle("#" + pullRequestStory.pullRequest.number + " " + status);
    adapter = new PullRequestDetailAdapter(getActivity(), getActivity().getLayoutInflater(),
        pullRequestStory, issueInfo.repoInfo, issueInfo.repoInfo.permissions, this);
    adapter.setIssueDetailRequestListener(this);
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
    new MaterialDialog.Builder(getActivity()).title(R.string.edit_issue_title)
        .input(null, pullRequestStory.pullRequest.title, false, (materialDialog, charSequence) -> {

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
    String body =
        pullRequestStory.pullRequest.body != null ? pullRequestStory.pullRequest.body.replace("\n",
            "<br />") : "";
    Intent launcherIntent =
        ContentEditorActivity.createLauncherIntent(getActivity(), issueInfo.repoInfo, issueInfo.num,
            getString(R.string.edit_issue_body_hint), body, true, false);
    startActivityForResult(launcherIntent, ISSUE_BODY_EDIT);
  }

  private void executeEditIssue(EditIssueRequestDTO editIssueRequestDTO) {
    EditIssueClient client = new EditIssueClient(issueInfo, editIssueRequestDTO);
    client.observable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Issue>() {
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
    MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
    builder.title(R.string.merge_title);
    builder.content(head.label);
    builder.input(getString(R.string.merge_message), pullRequestStory.pullRequest.title, false,
        (materialDialog, charSequence) -> {
          merge(charSequence.toString(), head.sha, issueInfo);
        });
    builder.inputType(InputType.TYPE_CLASS_TEXT);
    builder.show();
  }

  private void merge(String message, String sha, IssueInfo issueInfo) {
    MergeButtonRequest mergeButtonRequest = new MergeButtonRequest();
    mergeButtonRequest.commit_message = message;
    mergeButtonRequest.sha = sha;
    MergePullRequestClient mergePullRequestClient =
        new MergePullRequestClient(issueInfo, mergeButtonRequest);
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
            getContent();
          }
        });
  }
}

package com.alorma.github.ui.view.pullrequest;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alorma.github.R;
import com.alorma.github.StoreCredentials;
import com.alorma.github.sdk.bean.dto.response.GithubStatusResponse;
import com.alorma.github.sdk.bean.dto.response.Head;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.IssueState;
import com.alorma.github.sdk.bean.dto.response.Permissions;
import com.alorma.github.sdk.bean.dto.response.PullRequest;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.activity.ProfileActivity;
import com.alorma.github.ui.activity.RepoDetailActivity;
import com.alorma.github.ui.listeners.IssueDetailRequestListener;
import com.alorma.github.ui.view.UserAvatarView;
import com.alorma.github.ui.view.issue.ReactionsView;
import com.alorma.github.utils.TimeUtils;
import com.gh4a.utils.UiUtils;
import com.github.mobile.util.HtmlUtils;
import com.github.mobile.util.HttpImageGetter;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;

public class PullRequestDetailView extends LinearLayout {

  private PullRequest pullRequest;

  private TextView title;
  private TextView body;
  private UserAvatarView profileIcon;
  private TextView profileName;
  private TextView createdAt;
  private TextView mergeButton;
  private TextView textRepository;

  private PullRequestActionsListener pullRequestActionsListener;
  private ReactionsView reactionsLy;

  public PullRequestDetailView(Context context) {
    super(context);
    init();
  }

  public PullRequestDetailView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public PullRequestDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public PullRequestDetailView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  private void init() {
    inflate(getContext(), R.layout.pullrequest_detail_issue_view, this);
    setOrientation(VERTICAL);
    title = (TextView) findViewById(R.id.textTitle);
    body = (TextView) findViewById(R.id.textBody);
    profileIcon = (UserAvatarView) findViewById(R.id.profileIcon);
    profileName = (TextView) findViewById(R.id.name);
    createdAt = (TextView) findViewById(R.id.time);
    textRepository = (TextView) findViewById(R.id.textRepository);
    mergeButton = (TextView) findViewById(R.id.mergeButton);
    reactionsLy = (ReactionsView) findViewById(R.id.reactionsLy);
  }

  public void setPullRequest(final RepoInfo repoInfo, final PullRequest pullRequest, GithubStatusResponse statusResponse,
      Permissions permissions) {
    long milis = System.currentTimeMillis();
    if (this.pullRequest == null) {
      this.pullRequest = pullRequest;
      parseTitle(pullRequest);
      parseReactions(pullRequest);
      parseUser(pullRequest);
      parseBody(repoInfo, pullRequest);
      parseRepository(pullRequest);
      parseMergeButton(pullRequest, permissions);
      checkEditable(repoInfo, pullRequest);
    }
    Log.i("PR_time_detail", (System.currentTimeMillis() - milis) + "ms");
  }

  private void checkEditable(RepoInfo repoInfo, PullRequest pullRequest) {
    StoreCredentials credentials = new StoreCredentials(getContext());
    if (repoInfo.permissions != null && repoInfo.permissions.push || pullRequest.user.login.equals(credentials.getUserName())) {
      OnClickListener editClickListener = v -> {
        if (pullRequestActionsListener != null) {
          if (v.getId() == R.id.textTitle) {
            pullRequestActionsListener.onTitleEditRequest();
          } else if (v.getId() == R.id.textBody) {
            pullRequestActionsListener.onContentEditRequest();
          }
        }
      };

      title.setOnClickListener(editClickListener);
      body.setOnClickListener(editClickListener);
    }
  }

  private void parseMergeButton(PullRequest pullRequest, Permissions permissions) {
    if (mergeButton != null) {
      if (pullRequest.state == IssueState.closed
          || pullRequest.merged
          || permissions == null
          || !permissions.push
          || pullRequest.mergeable == null) {
        mergeButton.setVisibility(View.GONE);
      } else if (pullRequest.mergeable) {
        mergeButton.setVisibility(View.VISIBLE);
        mergeButton.setText(R.string.pullrequest_merge_action_valid);
        mergeButton.setBackgroundResource(R.drawable.pull_request_merge_valid);
        mergeButton.setOnClickListener(v -> {
          if (pullRequestActionsListener != null) {
            pullRequestActionsListener.mergeRequest(pullRequest.head, pullRequest.base);
          }
        });
      } else {
        mergeButton.setVisibility(View.VISIBLE);
        mergeButton.setText(R.string.pullrequest_merge_action_invalid);
        mergeButton.setBackgroundResource(R.drawable.pull_request_merge_invalid);
      }
    }
  }

  private void parseRepository(PullRequest pullRequest) {
    if (textRepository != null) {
      final Repo repo = pullRequest.repository;
      if (repo != null) {
        textRepository.setCompoundDrawables(getIcon(Octicons.Icon.oct_repo), null, null, null);
        textRepository.setText(repo.full_name);
        textRepository.setVisibility(View.VISIBLE);
        textRepository.setOnClickListener(v -> {
          RepoInfo repoInfo1 = new RepoInfo();
          repoInfo1.owner = repo.owner.login;
          repoInfo1.name = repo.name;
          Intent launcherIntent = RepoDetailActivity.createLauncherIntent(v.getContext(), repoInfo1);
          v.getContext().startActivity(launcherIntent);
        });
      } else {
        textRepository.setVisibility(View.GONE);
      }
    }
  }

  private void parseBody(RepoInfo repoInfo, PullRequest pullRequest) {
    if (pullRequest.body_html != null) {
      String htmlCode = HtmlUtils.format(pullRequest.body_html).toString();
      HttpImageGetter imageGetter = new HttpImageGetter(getContext());

      imageGetter.repoInfo(repoInfo);
      imageGetter.bind(body, htmlCode, pullRequest.number);

      body.setMovementMethod(UiUtils.CHECKING_LINK_METHOD);
    }
  }

  private void parseUser(PullRequest pullRequest) {
    if (pullRequest.user != null) {
      profileName.setText(pullRequest.user.login);
      createdAt.setText(TimeUtils.getTimeAgoString(pullRequest.created_at));

      profileIcon.setUser(pullRequest.user);

      OnClickListener issueUserClick = v -> {
        Intent launcherIntent = ProfileActivity.createLauncherIntent(v.getContext(), pullRequest.user);
        v.getContext().startActivity(launcherIntent);
      };
      profileName.setOnClickListener(issueUserClick);
      createdAt.setOnClickListener(issueUserClick);
      profileIcon.setOnClickListener(issueUserClick);
    }
  }

  private void parseTitle(PullRequest pullRequest) {
    title.setText(pullRequest.title);
  }

  private void parseReactions(Issue issue) {
    if (reactionsLy != null) {
      if (issue.reactions != null) {
        reactionsLy.setReactions(issue.reactions);
      } else {
        reactionsLy.setVisibility(View.GONE);
      }
    }
  }

  private IconicsDrawable getIcon(IIcon icon) {
    return new IconicsDrawable(getContext(), icon).actionBar().colorRes(getColorIcons());
  }

  public int getColorIcons() {
    if (pullRequest.merged) {
      return R.color.pullrequest_state_merged;
    } else if (pullRequest.state == IssueState.open) {
      return R.color.pullrequest_state_open;
    } else {
      return R.color.pullrequest_state_close;
    }
  }

  public void setPullRequestActionsListener(PullRequestActionsListener pullRequestActionsListener) {
    this.pullRequestActionsListener = pullRequestActionsListener;
  }

  public interface PullRequestActionsListener extends IssueDetailRequestListener {
    void mergeRequest(Head head, Head base);
  }
}
package com.alorma.github.ui.adapter.issues.holders;

import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.Permissions;
import com.alorma.github.sdk.bean.dto.response.PullRequest;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.bean.issue.IssueStoryDetail;
import com.alorma.github.sdk.bean.issue.PullRequestStory;
import com.alorma.github.ui.listeners.IssueDetailRequestListener;
import com.alorma.github.ui.view.pullrequest.PullRequestDetailView;

public class PullRequestHolder extends Holder {
  private final PullRequestDetailView pullRequestDetailView;
  private PullRequestStory story;
  private Permissions permissions;
  private final IssueDetailRequestListener issueDetailRequestListener;
  private final PullRequestDetailView.PullRequestActionsListener pullRequestActionsListener;

  public PullRequestHolder(PullRequestDetailView pullRequestDetailView, PullRequestStory story,
      Permissions permissions, IssueDetailRequestListener issueDetailRequestListener,
      PullRequestDetailView.PullRequestActionsListener pullRequestActionsListener) {
    super(pullRequestDetailView);
    this.pullRequestDetailView = pullRequestDetailView;
    this.story = story;
    this.permissions = permissions;
    this.issueDetailRequestListener = issueDetailRequestListener;
    this.pullRequestActionsListener = pullRequestActionsListener;
  }

  @Override
  public void setIssue(RepoInfo repoInfo, Issue issue) {
    if (issue instanceof PullRequest) {
      pullRequestDetailView.setPullRequest(repoInfo, story.pullRequest, story.statusResponse,
          permissions);
      pullRequestDetailView.setIssueDetailRequestListener(issueDetailRequestListener);
      pullRequestDetailView.setPullRequestActionsListener(pullRequestActionsListener);
    }
  }

  @Override
  public void setDetail(IssueStoryDetail detail) {

  }
}

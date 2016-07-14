package com.alorma.github.ui.adapter.issues.holders;

import com.alorma.github.sdk.bean.dto.response.Permissions;
import com.alorma.github.sdk.bean.dto.response.PullRequest;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.bean.issue.IssueStoryDetail;
import com.alorma.github.sdk.bean.issue.Story;
import com.alorma.github.ui.view.pullrequest.PullRequestDetailView;

public class PullRequestHolder extends Holder<PullRequest> {
  private final PullRequestDetailView pullRequestDetailView;
  private final PullRequestDetailView.PullRequestActionsListener pullRequestActionsListener;
  private Story<PullRequest> story;
  private Permissions permissions;

  public PullRequestHolder(PullRequestDetailView pullRequestDetailView, Story<PullRequest> story, Permissions permissions,
      PullRequestDetailView.PullRequestActionsListener pullRequestActionsListener) {
    super(pullRequestDetailView);
    this.pullRequestDetailView = pullRequestDetailView;
    this.story = story;
    this.permissions = permissions;
    this.pullRequestActionsListener = pullRequestActionsListener;
  }

  @Override
  public void setIssue(RepoInfo repoInfo, PullRequest pullRequest) {
    pullRequestDetailView.setPullRequest(repoInfo, story.item, story.item.statusResponse, permissions);
    pullRequestDetailView.setPullRequestActionsListener(pullRequestActionsListener);
  }

  @Override
  public void setDetail(IssueStoryDetail detail) {

  }
}

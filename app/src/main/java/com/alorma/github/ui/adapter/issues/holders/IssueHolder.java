package com.alorma.github.ui.adapter.issues.holders;

import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.bean.issue.IssueStoryDetail;
import com.alorma.github.ui.listeners.IssueDetailRequestListener;
import com.alorma.github.ui.view.issue.IssueDetailView;

public class IssueHolder extends Holder<Issue> {

  private IssueDetailRequestListener issueDetailRequestListener;
  private final IssueDetailView issueDetailView;

  public IssueHolder(IssueDetailView issueDetailView,
      IssueDetailRequestListener issueDetailRequestListener) {
    super(issueDetailView);
    this.issueDetailRequestListener = issueDetailRequestListener;
    this.issueDetailView = issueDetailView;
    issueDetailView.setIssueDetailRequestListener(this.issueDetailRequestListener);
  }

  @Override
  public void setIssue(RepoInfo repoInfo, Issue issue) {
    issueDetailView.setIssue(repoInfo, issue);
  }

  @Override
  public void setDetail(IssueStoryDetail detail) {

  }
}

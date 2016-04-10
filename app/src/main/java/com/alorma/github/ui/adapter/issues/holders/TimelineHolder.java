package com.alorma.github.ui.adapter.issues.holders;

import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.bean.issue.IssueStoryDetail;
import com.alorma.github.sdk.bean.issue.IssueStoryEvent;
import com.alorma.github.ui.view.issue.IssueTimelineView;

public class TimelineHolder extends Holder {
  private final IssueTimelineView issueTimelineView;

  public TimelineHolder(IssueTimelineView itemView) {
    super(itemView);
    issueTimelineView = itemView;
  }

  @Override
  public void setIssue(RepoInfo repoInfo, Issue issue) {

  }

  @Override
  public void setDetail(IssueStoryDetail detail) {
    if (detail instanceof IssueStoryEvent) {
      issueTimelineView.setIssueEvent((IssueStoryEvent) detail);
    }
  }
}

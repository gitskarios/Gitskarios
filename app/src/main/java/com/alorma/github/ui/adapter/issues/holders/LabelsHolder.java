package com.alorma.github.ui.adapter.issues.holders;

import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.bean.issue.IssueStoryDetail;
import com.alorma.github.sdk.bean.issue.IssueStoryLabelList;
import com.alorma.github.sdk.bean.issue.IssueStoryUnlabelList;
import com.alorma.github.ui.view.issue.IssueStoryLabelDetailView;

public class LabelsHolder extends Holder {
  private final IssueStoryLabelDetailView itemView;

  public LabelsHolder(IssueStoryLabelDetailView itemView) {
    super(itemView);
    this.itemView = itemView;
  }

  @Override
  public void setIssue(RepoInfo repoInfo, Issue issue) {

  }

  @Override
  public void setDetail(IssueStoryDetail detail) {
    if (detail instanceof IssueStoryLabelList) {
      itemView.setLabelsEvent((IssueStoryLabelList) detail);
    } else if (detail instanceof IssueStoryUnlabelList) {
      itemView.setLabelsEvent((IssueStoryUnlabelList) detail);
    }
  }
}
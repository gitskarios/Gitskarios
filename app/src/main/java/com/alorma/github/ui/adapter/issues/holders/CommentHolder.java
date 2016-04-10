package com.alorma.github.ui.adapter.issues.holders;

import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.bean.issue.IssueStoryComment;
import com.alorma.github.sdk.bean.issue.IssueStoryDetail;
import com.alorma.github.ui.view.issue.IssueCommentView;

public class CommentHolder extends Holder {
  private final IssueCommentView issueCommentView;
  private RepoInfo repoInfo;

  public CommentHolder(IssueCommentView itemView) {
    super(itemView);
    issueCommentView = itemView;
  }

  @Override
  public void setIssue(RepoInfo repoInfo, Issue issue) {
    this.repoInfo = repoInfo;
  }

  @Override
  public void setDetail(IssueStoryDetail detail) {
    if (detail instanceof IssueStoryComment) {
      issueCommentView.setComment(repoInfo, (IssueStoryComment) detail);
    }
  }
}
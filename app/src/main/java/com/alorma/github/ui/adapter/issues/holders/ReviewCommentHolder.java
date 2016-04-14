package com.alorma.github.ui.adapter.issues.holders;

import android.view.View;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.ReviewComment;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.bean.issue.IssueStoryDetail;
import com.alorma.github.sdk.bean.issue.IssueStoryReviewComment;
import com.alorma.github.ui.view.issue.ReviewCommentView;

public class ReviewCommentHolder extends Holder<Issue> {
  private final ReviewCommentView commentView;
  private RepoInfo repoInfo;

  public ReviewCommentHolder(View itemView) {
    super(itemView);
    commentView = (ReviewCommentView) itemView;
  }

  @Override
  public void setIssue(RepoInfo repoInfo, Issue issue) {

    this.repoInfo = repoInfo;
  }

  @Override
  public void setDetail(IssueStoryDetail detail) {
    if (detail instanceof IssueStoryReviewComment) {
      commentView.setReviewCommit((IssueStoryReviewComment) detail, repoInfo);
    }
  }
}

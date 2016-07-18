package com.alorma.github.ui.listeners;

import com.alorma.github.sdk.bean.issue.IssueStoryComment;

public interface IssueCommentRequestListener {
    void onContentEditRequest(IssueStoryComment issueStoryComment);
  }
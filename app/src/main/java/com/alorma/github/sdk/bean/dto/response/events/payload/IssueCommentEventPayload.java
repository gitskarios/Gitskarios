package com.alorma.github.sdk.bean.dto.response.events.payload;

import com.alorma.github.sdk.bean.dto.response.Issue;
import core.GithubComment;

public class IssueCommentEventPayload extends ActionEventPayload {
  public Issue issue;
  public GithubComment comment;

  public IssueCommentEventPayload() {
  }
}

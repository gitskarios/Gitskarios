package com.alorma.github.sdk.bean.issue;

import com.alorma.github.sdk.bean.dto.response.Commit;
import core.User;

public class PullRequestStoryCommit implements IssueStoryDetail {

  public Commit commit;
  public long created_at;

  public PullRequestStoryCommit(Commit commit) {
    this.commit = commit;
  }

  @Override
  public boolean isList() {
    return false;
  }

  @Override
  public String getType() {
    return "committed";
  }

  @Override
  public long createdAt() {
    return created_at;
  }

  @Override
  public User user() {
    return commit.committer;
  }

}

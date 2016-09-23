package com.alorma.github.sdk.bean.issue;

import core.User;

public class IssueStoryEvent implements IssueStoryDetail {
  public IssueEvent event;
  public long created_at;

  public IssueStoryEvent(IssueEvent event) {
    this.event = event;
  }

  @Override
  public boolean isList() {
    return false;
  }

  @Override
  public String getType() {
    return event.event;
  }

  @Override
  public long createdAt() {
    return created_at;
  }

  @Override
  public User user() {
    if (event.assigner != null) {
      return event.assigner;
    }
    return event.actor;
  }
}

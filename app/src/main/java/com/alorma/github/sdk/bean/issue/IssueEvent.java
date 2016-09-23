package com.alorma.github.sdk.bean.issue;

import com.alorma.github.sdk.bean.dto.response.Milestone;
import core.User;
import core.issues.Label;

public class IssueEvent {
  public int id;
  public String url;
  public User actor;
  public String event;
  public String commit_id;
  public String created_at;
  public Label label;
  public Milestone milestone;
  public User assignee;
  public User assigner;
  public Rename rename;

  public IssueEvent() {
  }
}

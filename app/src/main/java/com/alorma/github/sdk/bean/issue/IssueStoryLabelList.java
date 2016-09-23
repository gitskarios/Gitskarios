package com.alorma.github.sdk.bean.issue;

import core.User;
import core.issues.Label;
import java.util.ArrayList;

public class IssueStoryLabelList extends ArrayList<Label> implements IssueStoryDetail {
  public long created_at;
  public User user;

  public IssueStoryLabelList() {
  }

  @Override
  public boolean isList() {
    return true;
  }

  @Override
  public String getType() {
    return "labeled";
  }

  @Override
  public long createdAt() {
    return created_at;
  }

  @Override
  public User user() {
    return user;
  }
}

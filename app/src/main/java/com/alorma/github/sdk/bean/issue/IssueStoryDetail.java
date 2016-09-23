package com.alorma.github.sdk.bean.issue;

import core.User;

public interface IssueStoryDetail {
  boolean isList();

  String getType();

  long createdAt();

  User user();
}

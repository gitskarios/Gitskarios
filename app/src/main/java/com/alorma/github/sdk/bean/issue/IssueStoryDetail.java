package com.alorma.github.sdk.bean.issue;

import com.alorma.github.sdk.bean.dto.response.User;

/**
 * Created by Bernat on 07/04/2015.
 */
public interface IssueStoryDetail {
  boolean isList();

  String getType();

  long createdAt();

  User user();
}

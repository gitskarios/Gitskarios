package com.alorma.github.utils;

import com.alorma.github.sdk.bean.dto.response.Issue;

public class IssueUtils {

  public boolean canComment(Issue issue) {
    return issue != null && !issue.locked || (issue != null
        && issue.repository != null
        && (issue.repository.permissions.push || issue.repository.permissions.admin));
  }
}

package com.alorma.github.sdk.bean.dto.response;

import core.User;

public class ReviewComment  {
  public String url;
  public int id;
  public String diff_hunk;
  public String path;
  public int position;
  public int original_position;
  public String commit_id;
  public String original_commit_id;
  public User user;
  public String body;
  public String created_at;
  public String updated_at;
  public String html_url;
  public String pull_request_url;

  public ReviewComment() {
  }
}

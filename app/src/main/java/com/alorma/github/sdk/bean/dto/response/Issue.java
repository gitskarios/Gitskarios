package com.alorma.github.sdk.bean.dto.response;

import com.google.gson.annotations.SerializedName;
import core.User;
import core.issues.Label;
import core.repositories.Repo;
import java.util.List;

public class Issue extends GithubComment {
  public int number;
  public IssueState state;
  public boolean locked;
  public String title;
  public List<Label> labels;
  public User assignee;
  public List<User> assignees;
  public Milestone milestone;
  public int comments;
  @SerializedName("pull_request") public PullRequest pullRequest;
  @SerializedName("closed_at") public String closedAt;

  public Repo repository;

  public Issue() {

  }

}

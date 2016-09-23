package com.alorma.github.sdk.bean.dto.response.events.payload;

import com.alorma.github.sdk.bean.dto.response.Commit;
import com.alorma.github.sdk.bean.dto.response.CommitComment;
import com.alorma.github.sdk.bean.dto.response.GithubPage;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.PullRequest;
import com.alorma.github.sdk.bean.dto.response.Team;
import com.google.gson.annotations.SerializedName;
import core.User;
import core.repositories.Repo;
import core.repositories.releases.Release;
import java.util.List;

public class Payload {

  public String action;
  public Repo repository;
  public User sender;
  public int number;
  public PullRequest pull_request;
  @SerializedName("public") public boolean is_public;
  public User org;
  public String created_at;
  public Issue issue;
  public CommitComment comment;
  public Release release;
  public Team team;
  public long push_id;
  public int size;
  public int distinct_size;
  public String ref_type;
  public String ref;
  public String head;
  public String before;
  public List<Commit> commits;
  public Repo forkee;
  public User member;
  public String url;
  public List<GithubPage> pages;

  public Payload() {
  }
}

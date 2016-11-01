package com.alorma.github.sdk.bean.dto.response.events.payload;

import com.alorma.github.sdk.bean.dto.response.CommitComment;
import com.alorma.github.sdk.bean.dto.response.GithubPage;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.PullRequest;
import com.alorma.github.sdk.bean.dto.response.Team;
import com.google.gson.annotations.SerializedName;
import core.User;
import core.repositories.Commit;
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

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public Repo getRepository() {
    return repository;
  }

  public void setRepository(Repo repository) {
    this.repository = repository;
  }

  public User getSender() {
    return sender;
  }

  public void setSender(User sender) {
    this.sender = sender;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public PullRequest getPull_request() {
    return pull_request;
  }

  public void setPull_request(PullRequest pull_request) {
    this.pull_request = pull_request;
  }

  public boolean is_public() {
    return is_public;
  }

  public void setIs_public(boolean is_public) {
    this.is_public = is_public;
  }

  public User getOrg() {
    return org;
  }

  public void setOrg(User org) {
    this.org = org;
  }

  public String getCreated_at() {
    return created_at;
  }

  public void setCreated_at(String created_at) {
    this.created_at = created_at;
  }

  public Issue getIssue() {
    return issue;
  }

  public void setIssue(Issue issue) {
    this.issue = issue;
  }

  public CommitComment getComment() {
    return comment;
  }

  public void setComment(CommitComment comment) {
    this.comment = comment;
  }

  public Release getRelease() {
    return release;
  }

  public void setRelease(Release release) {
    this.release = release;
  }

  public Team getTeam() {
    return team;
  }

  public void setTeam(Team team) {
    this.team = team;
  }

  public long getPush_id() {
    return push_id;
  }

  public void setPush_id(long push_id) {
    this.push_id = push_id;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public int getDistinct_size() {
    return distinct_size;
  }

  public void setDistinct_size(int distinct_size) {
    this.distinct_size = distinct_size;
  }

  public String getRef_type() {
    return ref_type;
  }

  public void setRef_type(String ref_type) {
    this.ref_type = ref_type;
  }

  public String getRef() {
    return ref;
  }

  public void setRef(String ref) {
    this.ref = ref;
  }

  public String getHead() {
    return head;
  }

  public void setHead(String head) {
    this.head = head;
  }

  public String getBefore() {
    return before;
  }

  public void setBefore(String before) {
    this.before = before;
  }

  public List<Commit> getCommits() {
    return commits;
  }

  public void setCommits(List<Commit> commits) {
    this.commits = commits;
  }

  public Repo getForkee() {
    return forkee;
  }

  public void setForkee(Repo forkee) {
    this.forkee = forkee;
  }

  public User getMember() {
    return member;
  }

  public void setMember(User member) {
    this.member = member;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public List<GithubPage> getPages() {
    return pages;
  }

  public void setPages(List<GithubPage> pages) {
    this.pages = pages;
  }
}

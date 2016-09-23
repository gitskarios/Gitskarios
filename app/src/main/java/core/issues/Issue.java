package core.issues;

import com.google.gson.annotations.SerializedName;
import core.User;
import core.repositories.Repo;
import java.util.List;

public class Issue {
  private long id;
  private int number;
  private String title;
  private User user;
  private IssueState state;
  private Milestone milestone;
  private int comments;
  @SerializedName("created_at")
  private String createdAt;
  private Repo repository;
  @SerializedName("pull_request")
  private PullRequest pullRequest;
  @SerializedName("repository_url")
  private String repositoryUrl;

  public List<Label> labels;

  public long getId() {
    return id;
  }

  public int getNumber() {
    return number;
  }

  public String getTitle() {
    return title;
  }

  public User getUser() {
    return user;
  }

  public IssueState getState() {
    return state;
  }

  public Milestone getMilestone() {
    return milestone;
  }

  public int getComments() {
    return comments;
  }

  public List<Label> getLabels() {
    return labels;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public Repo getRepository() {
    return repository;
  }

  public PullRequest getPullRequest() {
    return pullRequest;
  }

  public String getRepositoryUrl() {
    return repositoryUrl;
  }

  public void setRepositoryUrl(String repositoryUrl) {
    this.repositoryUrl = repositoryUrl;
  }

  public void setRepository(Repo repository) {
    this.repository = repository;
  }
}

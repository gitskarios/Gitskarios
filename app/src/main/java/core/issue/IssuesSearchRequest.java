package core.issue;

import java.security.InvalidParameterException;

public class IssuesSearchRequest {
  private boolean isOpen;
  private Boolean isPullRequest;
  private Boolean isPublic;
  private String action;
  private String author;
  private String user;
  private String repo;

  public IssuesSearchRequest() {

  }

  public String build() {
    StringBuilder builder = new StringBuilder();
    builder.append("is:");
    if (isOpen) {
      builder.append("open");
    } else {
      builder.append("closed");
    }

    if (isPullRequest != null) {
      builder.append(" ");
      builder.append("is:");
      if (isPullRequest) {
        builder.append("pr");
      } else {
        builder.append("issue");
      }
    } else {
      builder.append(" ");
      builder.append("is:");
      builder.append("issue");
    }

    if (isPublic != null) {
      builder.append(" ");
      builder.append("is:");
      if (isPublic) {
        builder.append("public");
      } else {
        builder.append("private");
      }
    }

    if (action != null && author != null) {
      builder.append(" ");
      builder.append(action).append(":").append(author);
    }

    if (repo != null) {
      if (user == null) {
        throw new InvalidParameterException("User parameter must not be null");
      }
      builder.append(" ");
      builder.append("repo:");
      builder.append(user);
      builder.append("/");
      builder.append(repo);
    } else if (user != null) {
      builder.append(" ");
      builder.append("user:");
      builder.append(user);
    }
    return builder.toString();
  }

  public IssuesSearchRequest setIsOpen(boolean isOpen) {
    this.isOpen = isOpen;
    return this;
  }

  public IssuesSearchRequest setIsPullRequest(Boolean ispullRequest) {
    this.isPullRequest = ispullRequest;
    return this;
  }

  public IssuesSearchRequest setIsPublic(Boolean isPublic) {
    this.isPublic = isPublic;
    return this;
  }

  public IssuesSearchRequest setAuthor(String author) {
    this.author = author;
    return this;
  }

  public IssuesSearchRequest setAction(String action) {
    this.action = action;
    return this;
  }

  public IssuesSearchRequest setRepo(String repo) {
    this.repo = repo;
    return this;
  }

  public IssuesSearchRequest setUser(String user) {
    this.user = user;
    return this;
  }
}

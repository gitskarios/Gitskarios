package core.issue;

public class IssuesSearchRequest {
  private boolean isOpen;
  private Boolean isPullRequest;
  private Boolean isPublic;
  private String action;
  private String author;
  private String user;
  private String repo;

  private IssuesSearchRequest(boolean isOpen, Boolean isPullRequest, Boolean isPublic, String action, String author, String repo,
      String user) {
    this.repo = repo;
    this.isOpen = isOpen;
    this.isPullRequest = isPullRequest;
    this.isPublic = isPublic;
    this.action = action;
    this.author = author;
    this.user = user;
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
      builder.append(" ");
      builder.append("repo:");
      builder.append(author);
      builder.append("/");
      builder.append(repo);
    } else if (user != null) {
      builder.append(" ");
      builder.append("user:");
      builder.append(author);
    }
    return builder.toString();
  }

  public static class Builder {
    private boolean isOpen;
    private Boolean ispullRequest;
    private Boolean isPublic;
    private String author;
    public String action;
    private String repo;
    private String user;

    public Builder setIsOpen(boolean isOpen) {
      this.isOpen = isOpen;
      return this;
    }

    public Builder setIsPullRequest(Boolean ispullRequest) {
      this.ispullRequest = ispullRequest;
      return this;
    }

    public Builder setIsPublic(Boolean isPublic) {
      this.isPublic = isPublic;
      return this;
    }

    public Builder setAuthor(String author) {
      this.author = author;
      return this;
    }

    public Builder setAction(String action) {
      this.action = action;
      return this;
    }

    public Builder setRepo(String repo) {
      this.repo = repo;
      return this;
    }

    public Builder setUser(String user) {
      this.user = user;
      return this;
    }

    public IssuesSearchRequest build() {
      return new IssuesSearchRequest(isOpen, ispullRequest, isPublic, action, author, repo, user);
    }
  }
}

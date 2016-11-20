package core.issue;

public class IssuesSearchRequest {
  private boolean isOpen;
  private Boolean isPullRequest;
  private Boolean isPublic;
  private String action;
  private String author;
  private String repo;

  private IssuesSearchRequest(boolean isOpen, Boolean isPullRequest, Boolean isPublic, String action, String author, String repo) {
    this.repo = repo;
    this.isOpen = isOpen;
    this.isPullRequest = isPullRequest;
    this.isPublic = isPublic;
    this.action = action;
    this.author = author;
  }

  public String build() {
    StringBuilder builder = new StringBuilder();
    builder.append("is:");
    if (isOpen) {
      builder.append("open");
    } else {
      builder.append("close");
    }

    if (isPullRequest != null) {
      builder.append(" ");
      builder.append("is:");
      if (isPullRequest) {
        builder.append("pr");
      } else {
        builder.append("issue");
      }
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

    builder.append(" ");
    if (action != null && author != null) {
      builder.append(action).append(":").append(author);
    }

    if (repo != null) {
      builder.append(" ");
      builder.append("repo:");
      builder.append(author);
      builder.append("/");
      builder.append(repo);
    } else {
      builder.append(" ");
      builder.append("issues:");
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

    public IssuesSearchRequest build() {
      return new IssuesSearchRequest(isOpen, ispullRequest, isPublic, action, author, repo);
    }
  }
}

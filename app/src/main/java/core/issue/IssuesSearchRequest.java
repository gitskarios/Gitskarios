package core.issue;

public class IssuesSearchRequest {
  private boolean isOpen;
  private Boolean isPullRequest;
  private Boolean isPublic;
  private String action;
  private String author;

  private IssuesSearchRequest(boolean isOpen, Boolean isPullRequest, Boolean isPublic, String action, String author) {
    if (action == null || author == null) {
      throw new NullPointerException("Action or Author could not be null");
    }
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
    builder.append(action).append(":").append(author);
    return builder.toString();
  }

  public static class Builder {
    private boolean isOpen;
    private Boolean ispullRequest;
    private Boolean isPublic;
    private String author;
    public String action;

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

    public IssuesSearchRequest build() {
      return new IssuesSearchRequest(isOpen, ispullRequest, isPublic, action, author);
    }
  }
}

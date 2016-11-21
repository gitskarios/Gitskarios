package core.search;

public class IssuesSearchRequest extends SearchRequest{

  public IssuesSearchRequest setIsOpen(boolean isOpen) {
    add(new BooleanSearchItem("is", isOpen, "open", "closed"));
    return this;
  }

  public IssuesSearchRequest setIsPullRequest(Boolean isPullRequest) {
    add(new MandatoryBooleanSearchItem("type", isPullRequest, "pr", "issue"));
    return this;
  }

  public IssuesSearchRequest setIsPublic(Boolean isPublic) {
    add(new BooleanSearchItem("is", isPublic, "public", "private"));
    return this;
  }

  public IssuesSearchRequest setActionAndAuthor(String action, String author) {
    add(new KeyValueStringSearchItem(action, author));
    return this;
  }

  public IssuesSearchRequest setRepo(String repo, String userName) {
    add(new KeyValueStringSearchItem("repo", userName + "/" + repo));
    return this;
  }

  public IssuesSearchRequest setUser(String user) {
    add(new KeyValueStringSearchItem("user", user));
    return this;
  }
}

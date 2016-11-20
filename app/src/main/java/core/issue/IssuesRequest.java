package core.issue;

import com.alorma.github.sdk.bean.info.RepoInfo;
import java.util.Map;

public class IssuesRequest {
  private RepoInfo repoInfo;
  private Map<String, String> filters;

  public IssuesRequest(RepoInfo repoInfo, Map<String, String> filters) {
    this.repoInfo = repoInfo;
    this.filters = filters;
  }

  public RepoInfo getRepoInfo() {
    return repoInfo;
  }

  public void setRepoInfo(RepoInfo repoInfo) {
    this.repoInfo = repoInfo;
  }

  public Map<String, String> getFilters() {
    return filters;
  }

  public void setFilters(Map<String, String> filters) {
    this.filters = filters;
  }
}

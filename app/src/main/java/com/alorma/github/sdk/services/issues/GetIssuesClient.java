package com.alorma.github.sdk.services.issues;

import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.services.client.GithubListClient;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit.RestAdapter;

/**
 * Created by Bernat on 22/08/2014.
 */
public class GetIssuesClient extends GithubListClient<List<Issue>> {

  private final Map<String, String> filter;
  private final int page;
  private IssueInfo issueInfo;

  public GetIssuesClient() {
    this(null, 0);
  }
  public GetIssuesClient(int page) {
    this(null, page);
  }
  public GetIssuesClient(Map<String, String> filter) {
    this(null, filter, 0);
  }

  public GetIssuesClient(Map<String, String> filter, int page) {
    this(null, filter, page);
  }

  public GetIssuesClient(IssueInfo issueInfo, Map<String, String> filter) {
    this(issueInfo, filter, 0);
  }

  public GetIssuesClient(IssueInfo issueInfo, Map<String, String> filter, int page) {
    super();
    this.issueInfo = issueInfo;
    this.page = page;
    this.filter = filter != null ? filter : new HashMap<>();
  }

  @Override
  protected ApiSubscriber getApiObservable(RestAdapter restAdapter) {
    return new ApiSubscriber() {
      @Override
      protected void call(RestAdapter restAdapter) {
        IssuesService issuesService = restAdapter.create(IssuesService.class);
        if (page == 0) {
          if (issueInfo != null) {
            issuesService.issues(issueInfo.repoInfo.owner, issueInfo.repoInfo.name, filter, this);
          } else {
            issuesService.issues(filter, this);
          }
        } else {
          if (issueInfo != null) {
            issuesService.issues(issueInfo.repoInfo.owner, issueInfo.repoInfo.name, filter, page,
                this);
          } else {
            issuesService.issues(filter, page, this);
          }
        }
      }
    };
  }
}

package com.alorma.github.sdk.services.issues;

import com.alorma.github.sdk.bean.dto.request.IssueRequest;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.GithubClient;
import retrofit.RestAdapter;
import rx.Observable;

/**
 * Created by Bernat on 23/08/2014.
 */
public class PostNewIssueClient extends GithubClient<Issue> {

  private IssueRequest issue;
  private RepoInfo repoInfo;

  public PostNewIssueClient(RepoInfo repoInfo, IssueRequest issue) {
    super();
    this.repoInfo = repoInfo;
    this.repoInfo = repoInfo;
    this.issue = issue;
  }

  @Override
  protected Observable<Issue> getApiObservable(RestAdapter restAdapter) {
    if (issue == null || issue.title == null) {
      throw new RuntimeException("Issue or Issue title can not be null");
    }
    IssuesService service = restAdapter.create(IssuesService.class);
    return service.create(repoInfo.owner, repoInfo.name, issue);
  }

  @Override
  public String getAcceptHeader() {
    return "application/vnd.github.v3.text+json";
  }
}

package com.alorma.github.sdk.services.issues;

import com.alorma.github.sdk.bean.dto.request.IssueRequest;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.IssueState;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.services.client.GithubClient;
import retrofit.RestAdapter;
import rx.Observable;

/**
 * Created by Bernat on 01/09/2014.
 */
public class ChangeIssueStateClient extends GithubClient<Issue> {

  private final IssueRequest issueRequest;
  private IssueInfo info;

  public ChangeIssueStateClient(IssueInfo info, IssueState state) {
    super();
    this.info = info;

    this.issueRequest = new IssueRequest();
    issueRequest.state = state;
  }

  @Override
  protected Observable<Issue> getApiObservable(RestAdapter restAdapter) {
    return restAdapter.create(IssuesService.class)
        .closeIssue(info.repoInfo.owner, info.repoInfo.name, info.num, issueRequest);
  }
}

package com.alorma.github.sdk.services.pullrequest;

import com.alorma.github.sdk.bean.dto.response.PullRequest;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.services.client.GithubClient;
import retrofit.RestAdapter;
import rx.Observable;

public class GetSinglePullRequestClient extends GithubClient<PullRequest> {

  private final IssueInfo info;

  public GetSinglePullRequestClient(IssueInfo info) {
    super();
    this.info = info;
  }

  @Override
  protected Observable<PullRequest> getApiObservable(RestAdapter restAdapter) {
    return restAdapter.create(PullRequestsService.class)
        .pull(info.repoInfo.owner, info.repoInfo.name, info.num);
  }
}

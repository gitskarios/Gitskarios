package com.alorma.github.sdk.services.pullrequest;

import com.alorma.github.sdk.bean.dto.request.MergeButtonRequest;
import com.alorma.github.sdk.bean.dto.response.MergeButtonResponse;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.services.client.GithubClient;
import retrofit.RestAdapter;
import rx.Observable;

/**
 * Created by Bernat on 21/06/2015.
 */
public class MergePullRequestClient extends GithubClient<MergeButtonResponse> {
  private IssueInfo issueInfo;
  private MergeButtonRequest mergeButtonRequest;

  public MergePullRequestClient(IssueInfo issueInfo, MergeButtonRequest mergeButtonRequest) {
    super();
    this.issueInfo = issueInfo;
    this.mergeButtonRequest = mergeButtonRequest;
  }

  @Override
  protected Observable<MergeButtonResponse> getApiObservable(RestAdapter restAdapter) {
    return restAdapter.create(PullRequestsService.class)
        .merge(issueInfo.repoInfo.owner, issueInfo.repoInfo.name, issueInfo.num,
            mergeButtonRequest);
  }
}

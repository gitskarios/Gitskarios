package com.alorma.github.sdk.services.pullrequest;

import com.alorma.github.sdk.bean.dto.response.Commit;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.services.client.GithubListClient;
import java.util.List;
import retrofit.RestAdapter;

public class GetPullRequestCommits extends GithubListClient<List<Commit>> {

  private IssueInfo info;
  private int page;

  public GetPullRequestCommits(IssueInfo info) {
    this(info, 0);
  }

  public GetPullRequestCommits(IssueInfo info, int page) {
    super();
    this.info = info;
    this.page = page;
  }

  @Override
  protected ApiSubscriber getApiObservable(RestAdapter restAdapter) {
    return new ApiSubscriber() {
      @Override
      protected void call(RestAdapter restAdapter) {
        PullRequestsService pullRequestsService = restAdapter.create(PullRequestsService.class);
        if (page == 0) {
          pullRequestsService.commits(info.repoInfo.owner, info.repoInfo.name, info.num, this);
        } else {
          pullRequestsService.commits(info.repoInfo.owner, info.repoInfo.name, info.num, nextPage,
              this);
        }
      }
    };
  }

  @Override
  public String getAcceptHeader() {
    return "application/vnd.github.cryptographer-preview+sha.json";
  }
}

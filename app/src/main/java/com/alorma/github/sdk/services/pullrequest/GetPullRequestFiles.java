package com.alorma.github.sdk.services.pullrequest;

import com.alorma.github.sdk.bean.dto.response.CommitFile;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.services.client.GithubListClient;
import java.util.List;
import retrofit.RestAdapter;

/**
 * Created by Bernat on 17/06/2015.
 */
public class GetPullRequestFiles extends GithubListClient<List<CommitFile>> {

  private IssueInfo info;
  private int page;

  public GetPullRequestFiles(IssueInfo info) {
    super();
    this.info = info;
  }

  public GetPullRequestFiles(IssueInfo info, int page) {
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
          pullRequestsService.files(info.repoInfo.owner, info.repoInfo.name, info.num, this);
        } else {
          pullRequestsService.files(info.repoInfo.owner, info.repoInfo.name, info.num, page, this);
        }
      }
    };
  }
}

package com.alorma.github.sdk.services.commit;

import com.alorma.github.sdk.bean.dto.response.CommitComment;
import com.alorma.github.sdk.bean.info.CommitInfo;
import com.alorma.github.sdk.services.client.GithubListClient;
import java.util.List;
import retrofit.RestAdapter;

/**
 * Created by Bernat on 22/12/2014.
 */
public class GetCommitCommentsClient extends GithubListClient<List<CommitComment>> {

  private CommitInfo info;
  private int page = 0;

  public GetCommitCommentsClient(CommitInfo info) {
    super();
    this.info = info;
  }

  public GetCommitCommentsClient(CommitInfo info, int page) {
    super();
    this.info = info;
    this.page = page;
  }

  @Override
  public String getAcceptHeader() {
    return "application/vnd.github.v3.html+json";
  }

  @Override
  protected ApiSubscriber getApiObservable(RestAdapter restAdapter) {
    return new ApiSubscriber() {
      @Override
      protected void call(RestAdapter restAdapter) {
        CommitsService commitsService = restAdapter.create(CommitsService.class);
        if (page == 0) {
          commitsService.singleCommitComments(info.repoInfo.owner, info.repoInfo.name, info.sha,
              this);
        } else {
          commitsService.singleCommitComments(info.repoInfo.owner, info.repoInfo.name, info.sha,
              page, this);
        }
      }
    };
  }
}

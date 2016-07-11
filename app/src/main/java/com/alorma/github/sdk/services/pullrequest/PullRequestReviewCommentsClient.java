package com.alorma.github.sdk.services.pullrequest;

import com.alorma.github.sdk.bean.dto.response.ReviewComment;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.services.client.GithubListClient;
import java.util.List;
import retrofit.RestAdapter;

/**
 * Created by a557114 on 25/07/2015.
 */
public class PullRequestReviewCommentsClient extends GithubListClient<List<ReviewComment>> {

  private final int page;
  private IssueInfo info;

  public PullRequestReviewCommentsClient(IssueInfo info) {
    this(info, 0);
  }

  public PullRequestReviewCommentsClient(IssueInfo info, int page) {
    super();
    this.info = info;
    this.page = page;
  }

  @Override
  protected ApiSubscriber getApiObservable(RestAdapter restAdapter) {
    return new ApiSubscriber() {
      @Override
      protected void call(RestAdapter restAdapter) {
        PullRequestsService service = restAdapter.create(PullRequestsService.class);

        if (page == 0) {
          service.reviewComments(info.repoInfo.owner, info.repoInfo.name, info.num, this);
        } else {
          service.reviewComments(info.repoInfo.owner, info.repoInfo.name, info.num, page, this);
        }
      }
    };
  }

  @Override
  public String getAcceptHeader() {
    return "application/vnd.github.v3.full+json";
  }
}

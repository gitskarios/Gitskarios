package com.alorma.github.sdk.services.issues;

import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.services.client.GithubListClient;
import java.util.List;
import retrofit.RestAdapter;

public class UserIssuesClient extends GithubListClient<List<Issue>> {

  private final int page;

  public UserIssuesClient() {
    this(0);
  }

  public UserIssuesClient(int page) {
    this.page = page;
  }

  @Override
  protected ApiSubscriber getApiObservable(RestAdapter restAdapter) {
    return new ApiSubscriber() {
      @Override
      protected void call(RestAdapter restAdapter) {
        IssuesService issuesService = restAdapter.create(IssuesService.class);
        if (page == 0) {
          issuesService.userIssues(this);
        } else {
          issuesService.userIssues(page, this);
        }
      }
    };
  }
}

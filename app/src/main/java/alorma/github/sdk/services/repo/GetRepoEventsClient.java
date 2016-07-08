package com.alorma.github.sdk.services.repo;

import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.GithubListClient;
import java.util.List;
import retrofit.RestAdapter;

public class GetRepoEventsClient extends GithubListClient<List<GithubEvent>> {

  private int page;
  private RepoInfo info;

  public GetRepoEventsClient(RepoInfo info) {
    this(info, 0);
  }

  public GetRepoEventsClient(RepoInfo info, int page) {
    super();
    this.info = info;
    this.page = page;
  }

  @Override
  protected ApiSubscriber getApiObservable(RestAdapter restAdapter) {
    return new ApiSubscriber() {
      @Override
      protected void call(RestAdapter restAdapter) {
        RepoService repoService = restAdapter.create(RepoService.class);
        if (page == 0) {
          repoService.events(info.owner, info.name, this);
        } else {
          repoService.events(info.owner, info.name, page, this);
        }
      }
    };
  }
}

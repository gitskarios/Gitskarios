package com.alorma.github.sdk.services.repo;

import com.alorma.github.sdk.bean.dto.response.Release;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.GithubListClient;
import java.util.List;
import retrofit.RestAdapter;

/**
 * Created by a557114 on 29/07/2015.
 */
public class GetRepoReleasesClient extends GithubListClient<List<Release>> {
  private RepoInfo info;
  private int page;

  public GetRepoReleasesClient(RepoInfo info, int page) {
    super();
    this.info = info;
    this.page = page;
  }

  @Override
  protected ApiSubscriber getApiObservable(RestAdapter restAdapter) {
    return new ApiSubscriber() {
      @Override
      protected void call(RestAdapter restAdapter) {
        if (page == 0) {
          restAdapter.create(RepoService.class).releases(info.owner, info.name, this);
        } else {
          restAdapter.create(RepoService.class).releases(info.owner, info.name, page, this);
        }
      }
    };
  }
}

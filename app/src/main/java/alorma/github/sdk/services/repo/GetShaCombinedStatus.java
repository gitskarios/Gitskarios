package com.alorma.github.sdk.services.repo;

import com.alorma.github.sdk.bean.dto.response.GithubStatusResponse;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.GithubListClient;
import retrofit.RestAdapter;

/**
 * Created by a557114 on 06/09/2015.
 */
public class GetShaCombinedStatus extends GithubListClient<GithubStatusResponse> {

  private final RepoInfo repoInfo;
  private String ref;
  private int page;

  public GetShaCombinedStatus(RepoInfo repoInfo, String ref) {
    this(repoInfo, ref, 0);
  }

  public GetShaCombinedStatus(RepoInfo repoInfo, String ref, int page) {
    super();
    this.repoInfo = repoInfo;
    this.ref = ref;
    this.page = page;
  }

  @Override
  protected ApiSubscriber getApiObservable(RestAdapter restAdapter) {
    return new ApiSubscriber() {
      @Override
      protected void call(RestAdapter restAdapter) {
        RepoService repoService = restAdapter.create(RepoService.class);
        if (page == 0) {
          repoService.combinedStatusASync(getOwner(), getRepo(), ref, this);
        } else {
          repoService.combinedStatusASync(getOwner(), getRepo(), ref, page, this);
        }
      }
    };
  }

  private String getOwner() {
    return repoInfo.owner;
  }

  private String getRepo() {
    return repoInfo.name;
  }
}

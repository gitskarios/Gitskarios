package com.alorma.github.sdk.services.repo;

import com.alorma.github.sdk.bean.info.ReleaseInfo;
import com.alorma.github.sdk.services.client.GithubClient;
import core.repositories.releases.Release;
import retrofit.RestAdapter;
import rx.Observable;

public class GetReleaseClient extends GithubClient<Release> {
  private ReleaseInfo info;

  public GetReleaseClient(ReleaseInfo info) {
    super();
    this.info = info;
  }

  @Override
  protected Observable<Release> getApiObservable(RestAdapter restAdapter) {
    return restAdapter.create(RepoService.class).release(info.repoInfo.owner, info.repoInfo.name, String.valueOf(info.num));
  }
}

package com.alorma.github.sdk.services.repo;

import com.alorma.github.sdk.bean.dto.response.Release;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.GithubClient;
import retrofit.RestAdapter;
import rx.Observable;

/**
 * Created by a557114 on 29/07/2015.
 */
public class LastReleaseClient extends GithubClient<Release> {
  private RepoInfo info;

  public LastReleaseClient(RepoInfo info) {
    super();
    this.info = info;
  }

  @Override
  protected Observable<Release> getApiObservable(RestAdapter restAdapter) {
    return restAdapter.create(RepoService.class).lastRelease(info.owner, info.name);
  }
}

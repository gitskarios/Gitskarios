package com.alorma.github.sdk.services.repo;

import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.GithubClient;
import java.util.Map;
import retrofit.RestAdapter;
import rx.Observable;

public class GetRepoLanguages extends GithubClient<Map<String, Long>> {

  private RepoInfo repoInfo;

  public GetRepoLanguages(RepoInfo repoInfo) {
    this.repoInfo = repoInfo;
  }

  @Override
  protected Observable<Map<String, Long>> getApiObservable(RestAdapter restAdapter) {
    return restAdapter.create(RepoService.class).languages(repoInfo.owner, repoInfo.name);
  }
}

package com.alorma.github.sdk.services.repo;

import com.alorma.github.sdk.bean.info.RepoInfo;
import core.repositories.Repo;
import retrofit.RestAdapter;
import rx.Observable;

public class GetRepoClient extends GithubRepoClient<Repo> {

  public GetRepoClient(RepoInfo repoInfo) {
    super(repoInfo);
  }

  @Override
  protected Observable<Repo> getApiObservable(RestAdapter restAdapter) {
    return restAdapter.create(RepoService.class).get(getOwner(), getRepo());
  }

  @Override
  public String getAcceptHeader() {
    return "application/vnd.github.drax-preview+json";
  }
}

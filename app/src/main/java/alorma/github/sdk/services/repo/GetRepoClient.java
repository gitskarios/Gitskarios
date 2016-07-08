package com.alorma.github.sdk.services.repo;

import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import retrofit.RestAdapter;
import rx.Observable;

/**
 * Created by Bernat on 17/07/2014.
 */
public class GetRepoClient extends GithubRepoClient<Repo> {

  public GetRepoClient(RepoInfo repoInfo) {
    super(repoInfo);
  }

  @Override
  protected Observable<Repo> getApiObservable(RestAdapter restAdapter) {
    return restAdapter.create(RepoService.class).get(getOwner(), getRepo());
  }
}

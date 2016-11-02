package com.alorma.github.sdk.services.repo.actions;

import com.alorma.github.sdk.services.client.GithubClient;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import rx.Observable;

public class UnstarRepoClient extends GithubClient<Boolean> {

  private final String owner;
  private final String repo;

  public UnstarRepoClient(String owner, String repo) {
    super();
    this.owner = owner;
    this.repo = repo;
  }

  @Override
  protected Observable<Boolean> getApiObservable(RestAdapter restAdapter) {
    return restAdapter.create(RepoActionsService.class).unstarRepo(owner, repo).onErrorResumeNext(throwable -> {
      if (throwable instanceof RetrofitError) {
        return Observable.just(((RetrofitError) throwable).getResponse());
      }
      return Observable.error(throwable);
    }).map(r -> !(r != null && r.getStatus() == 204));
  }
}

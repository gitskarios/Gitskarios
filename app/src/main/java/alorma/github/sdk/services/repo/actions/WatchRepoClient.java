package com.alorma.github.sdk.services.repo.actions;

import com.alorma.github.sdk.bean.dto.request.WatchBodyRequest;
import com.alorma.github.sdk.services.client.GithubClient;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Bernat on 07/08/2014.
 */
public class WatchRepoClient extends GithubClient<Boolean> {

  private final String owner;
  private final String repo;

  public WatchRepoClient(String owner, String repo) {
    super();
    this.owner = owner;
    this.repo = repo;
  }

  @Override
  protected Observable<Boolean> getApiObservable(RestAdapter restAdapter) {
    WatchBodyRequest watchBodyRequest = new WatchBodyRequest();
    watchBodyRequest.subscribed = true;
    watchBodyRequest.ignored = false;
    return restAdapter.create(RepoActionsService.class)
        .watchRepo(owner, repo, watchBodyRequest)
        .onErrorResumeNext(new Func1<Throwable, Observable<? extends Response>>() {
          @Override
          public Observable<? extends Response> call(Throwable throwable) {
            if (throwable instanceof RetrofitError) {
              return Observable.just(((RetrofitError) throwable).getResponse());
            }
            return Observable.error(throwable);
          }
        })
        .map(new Func1<Response, Boolean>() {
          @Override
          public Boolean call(Response r) {
            return r != null && r.getStatus() == 200;
          }
        });
  }
}

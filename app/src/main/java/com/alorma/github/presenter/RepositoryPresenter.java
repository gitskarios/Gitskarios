package com.alorma.github.presenter;

import android.support.annotation.NonNull;
import com.alorma.github.cache.CacheWrapper;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.repo.GetRepoBranchesClient;
import com.alorma.github.sdk.services.repo.GetRepoClient;
import core.ApiClient;
import core.datasource.RestWrapper;
import core.repositories.Branch;
import core.repositories.Repo;
import core.repository.GenericRepository;
import java.util.List;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RepositoryPresenter extends Presenter<RepoInfo, Repo> {

  public RepositoryPresenter() {
  }

  @Override
  public void load(@NonNull final RepoInfo repoInfo, @NonNull final Callback<Repo> repoCallback) {

    GetRepoClient repoClient = new GetRepoClient(repoInfo);

    Observable<Repo> memory = Observable.create(new Observable.OnSubscribe<Repo>() {
      @Override
      public void call(Subscriber<? super Repo> subscriber) {
        try {
          if (!subscriber.isUnsubscribed()) {
            subscriber.onNext(CacheWrapper.getRepository(repoInfo));
            subscriber.onCompleted();
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    Observable<List<Branch>> branchesClient =
        new GetRepoBranchesClient(repoInfo).observable().subscribeOn(Schedulers.newThread());

    Observable<Repo> combinedWithBranches =
        Observable.combineLatest(repoClient.observable().subscribeOn(Schedulers.newThread()),
            branchesClient, (repo, branches) -> {
              repo.branches = branches;
              if (branches.size() == 1) {
                repo.setDefaultBranch(branches.get(0).name);
              }
              return repo;
            });

    Observable<Repo> repoObservable = combinedWithBranches.doOnNext(CacheWrapper::setRepository);

    Observable.concat(memory, repoObservable)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Repo>() {

          @Override
          public void onNext(Repo repo) {
            repoCallback.onResponse(repo, true);
          }

          @Override
          public void onError(Throwable e) {

          }

          @Override
          public void onCompleted() {

          }
        });
  }

  @Override
  public void loadMore(RepoInfo repoInfo, Callback<Repo> repoCallback) {

  }

  @Override
  protected GenericRepository<RepoInfo, Repo> configRepository(RestWrapper restWrapper) {
    return null;
  }

  @Override
  protected RestWrapper getRest(ApiClient apiClient, String token) {
    return null;
  }

  @Override
  public void action(Repo repo, Callback<Repo> repoCallback, boolean firstTime) {

  }
}

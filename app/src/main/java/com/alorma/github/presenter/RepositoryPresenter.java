package com.alorma.github.presenter;

import android.support.annotation.NonNull;

import com.alorma.github.cache.CacheWrapper;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.repo.GetRepoBranchesClient;
import com.alorma.github.sdk.services.repo.GetRepoClient;

import java.util.List;

import core.repositories.Branch;
import core.repositories.Repo;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RepositoryPresenter extends BaseRxPresenter<RepoInfo, Repo, View<Repo>> {

  public RepositoryPresenter(Scheduler mainScheduler, Scheduler ioScheduler) {
      super(mainScheduler, ioScheduler, null);
  }

  @Override
  public void execute(@NonNull final RepoInfo repoInfo) {
    if(!isViewAttached()) return;

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
        new GetRepoBranchesClient(repoInfo).observable().subscribeOn(ioScheduler);

    Observable<Repo> combinedWithBranches =
        Observable.combineLatest(repoClient.observable().subscribeOn(ioScheduler),
            branchesClient, (repo, branches) -> {
              repo.branches = branches;
              if (branches.size() == 1) {
                repo.setDefaultBranch(branches.get(0).name);
              }
              return repo;
            });

    Observable<Repo> repoObservable = combinedWithBranches.doOnNext(CacheWrapper::setRepository);

    Observable.concat(memory, repoObservable)
        .observeOn(mainScheduler)
        .subscribe(new Subscriber<Repo>() {

          @Override
          public void onNext(Repo repo) {
            getView().onDataReceived(repo, false);
          }

          @Override
          public void onError(Throwable e) {

          }

          @Override
          public void onCompleted() {

          }
        });
  }
}

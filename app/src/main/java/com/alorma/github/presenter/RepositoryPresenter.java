package com.alorma.github.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import com.alorma.github.cache.CacheWrapper;
import com.alorma.github.sdk.bean.dto.response.Branch;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.core.ApiClient;
import com.alorma.github.sdk.core.datasource.RestWrapper;
import com.alorma.github.sdk.core.repository.GenericRepository;
import com.alorma.github.sdk.services.repo.GetRepoBranchesClient;
import com.alorma.github.sdk.services.repo.GetRepoClient;
import java.util.List;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class RepositoryPresenter extends Presenter<RepoInfo, Repo> {

  private Context context;

  public RepositoryPresenter(Context context) {
    this.context = context;
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

    Observable<List<Branch>> branchesClient = new GetRepoBranchesClient(repoInfo).observable()
        .subscribeOn(Schedulers.newThread());

    Observable<Repo> combinedWithBranches =
        Observable.combineLatest(repoClient.observable().subscribeOn(Schedulers.newThread()),
            branchesClient, (repo, branches) -> {
              repo.branches = branches;
              if (branches.size() == 1) {
                repo.default_branch = branches.get(0).name;
              }
              return repo;
            });

    Observable<Repo> repoObservable = combinedWithBranches.doOnNext(CacheWrapper::setRepository);

    Observable.concat(memory, repoObservable)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Repo>() {

          @Override
          public void onNext(Repo repo) {
            repoCallback.onResponse(repo);
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
  protected GenericRepository<RepoInfo, Repo> configRepository(RestWrapper restWrapper) {
    return null;
  }

  @Override
  protected RestWrapper getRest(ApiClient apiClient, String token) {
    return null;
  }

  @Override
  protected ApiClient getApiClient() {
    return null;
  }

  @Override
  public void action(Repo repo, Callback<Repo> repoCallback) {

  }
}

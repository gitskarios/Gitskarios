package com.alorma.github.presenter.repos;

import android.support.annotation.NonNull;
import com.alorma.github.presenter.Presenter;
import com.alorma.github.sdk.core.ApiClient;
import com.alorma.github.sdk.core.Github;
import com.alorma.github.sdk.core.datasource.CacheDataSource;
import com.alorma.github.sdk.core.datasource.CloudDataSource;
import com.alorma.github.sdk.core.datasource.RestWrapper;
import com.alorma.github.sdk.core.datasource.SdkItem;
import com.alorma.github.sdk.core.repositories.Repo;
import com.alorma.github.sdk.core.repositories.RepositoriesRetrofitWrapper;
import com.alorma.github.sdk.core.repository.GenericRepository;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class RepositoriesPresenter extends Presenter<String, List<Repo>> {

  private String sortOrder;
  private Integer page;
  private GenericRepository<String, List<Repo>> genericRepository;

  public RepositoriesPresenter(String sortOrder) {
    this.sortOrder = sortOrder;
  }

  @Override
  public void load(String username, Callback<List<Repo>> listCallback) {
    execute(config().execute(new SdkItem<>(username)), listCallback);
  }

  @Override
  public void loadMore(String username, Callback<List<Repo>> listCallback) {
    if (page != null) {
      execute(config().execute(new SdkItem<>(page, username)), listCallback);
    }
  }

  private void execute(Observable<SdkItem<List<Repo>>> observable,
      Callback<List<Repo>> listCallback) {
    observable.subscribeOn(Schedulers.newThread())
        .map(sdkResponseObservable -> {
          this.page = sdkResponseObservable.getPage();
          return sdkResponseObservable.getK();
        })
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe(listCallback::showLoading)
        .doOnCompleted(listCallback::hideLoading)
        .subscribe(repos -> action(repos, listCallback), Throwable::printStackTrace);
  }

  @Override
  public void action(List<Repo> repos, Callback<List<Repo>> listCallback) {
    if (repos != null && repos.size() > 0) {
      listCallback.onResponse(repos);
    } else {
      listCallback.onResponseEmpty();
    }
  }

  @NonNull
  @Override
  protected GenericRepository<String, List<Repo>> configRepository(RestWrapper restWrapper) {
    if (genericRepository == null) {
      genericRepository = new GenericRepository<>(getUserReposCacheDataSource(),
          getCloudRepositoriesDataSource(restWrapper, sortOrder));
    }
    return genericRepository;
  }

  @NonNull
  @Override
  protected RepositoriesRetrofitWrapper getRest(ApiClient apiClient, String token) {
    return new RepositoriesRetrofitWrapper(apiClient, token);
  }

  @NonNull
  @Override
  protected ApiClient getApiClient() {
    return new Github();
  }

  protected abstract CacheDataSource<String, List<Repo>> getUserReposCacheDataSource();

  protected abstract CloudDataSource<String, List<Repo>> getCloudRepositoriesDataSource(
      RestWrapper reposRetrofit, String sortOrder);
}

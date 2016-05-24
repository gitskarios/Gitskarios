package com.alorma.github.presenter.repos;

import android.support.annotation.NonNull;
import com.alorma.github.injector.SortOrder;
import com.alorma.github.presenter.Presenter;
import com.alorma.github.sdk.core.ApiClient;
import com.alorma.github.sdk.core.datasource.CacheDataSource;
import com.alorma.github.sdk.core.datasource.CloudDataSource;
import com.alorma.github.sdk.core.datasource.RestWrapper;
import com.alorma.github.sdk.core.datasource.SdkItem;
import com.alorma.github.sdk.core.repositories.Repo;
import com.alorma.github.sdk.core.repositories.RepositoriesRetrofitWrapper;
import com.alorma.github.sdk.core.repository.GenericRepository;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class RepositoriesPresenter extends Presenter<String, List<Repo>> {

  @Inject @SortOrder String sortOrder;
  private Integer page;
  private GenericRepository<String, List<Repo>> genericRepository;

  public RepositoriesPresenter() {

  }

  @Override
  public void load(String username, Callback<List<Repo>> listCallback) {
    execute(config().execute(new SdkItem<>(username)), listCallback, true);
  }

  @Override
  public void loadMore(String username, Callback<List<Repo>> listCallback) {
    if (page != null) {
      execute(config().execute(new SdkItem<>(page, username)), listCallback, false);
    }
  }

  private void execute(Observable<SdkItem<List<Repo>>> observable,
      Callback<List<Repo>> listCallback, boolean firstTime) {
    observable
        .timeout(20, TimeUnit.SECONDS)
        .retry(3).subscribeOn(Schedulers.newThread())
        .map(sdkResponseObservable -> {
          if (sdkResponseObservable.getPage() != null && sdkResponseObservable.getPage() > 0) {
            this.page = sdkResponseObservable.getPage();
          } else {
            this.page = null;
          }
          return sdkResponseObservable.getK();
        })
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe(listCallback::showLoading)
        .doOnCompleted(listCallback::hideLoading)
        .subscribe(repos -> action(repos, listCallback, firstTime), throwable -> {
          listCallback.onResponseEmpty();
          listCallback.hideLoading();
          throwable.printStackTrace();
        });
  }

  @Override
  public void action(List<Repo> repos, Callback<List<Repo>> listCallback, boolean firstTime) {
    if (repos != null && repos.size() > 0) {
      listCallback.onResponse(repos, firstTime);
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

  protected abstract CacheDataSource<String, List<Repo>> getUserReposCacheDataSource();

  protected abstract CloudDataSource<String, List<Repo>> getCloudRepositoriesDataSource(
      RestWrapper reposRetrofit, String sortOrder);
}

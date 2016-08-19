package com.alorma.github.presenter.issue;

import android.support.annotation.NonNull;
import com.alorma.github.presenter.Presenter;
import com.alorma.github.sdk.core.ApiClient;
import com.alorma.github.sdk.core.datasource.CacheDataSource;
import com.alorma.github.sdk.core.datasource.CloudDataSource;
import com.alorma.github.sdk.core.datasource.RestWrapper;
import com.alorma.github.sdk.core.datasource.SdkItem;
import com.alorma.github.sdk.core.issues.Issue;
import com.alorma.github.sdk.core.issues.IssuesRetrofitWrapper;
import com.alorma.github.sdk.core.repository.GenericRepository;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class UserIssuesPresenter extends Presenter<Void, List<Issue>> {

  private Integer page;
  private GenericRepository<Void, List<Issue>> genericRepository;

  public UserIssuesPresenter() {

  }

  @Override
  public void load(Void aVoid, Callback<List<Issue>> listCallback) {
    execute(config().execute(new SdkItem<>(null)), listCallback, true);
  }

  @Override
  public void loadMore(Void aVoid, Callback<List<Issue>> listCallback) {
    if (page != null) {
      execute(config().execute(new SdkItem<>(page, null)), listCallback, false);
    }
  }

  private void execute(Observable<SdkItem<List<Issue>>> observable, Callback<List<Issue>> listCallback, boolean firstTime) {
    observable.timeout(20, TimeUnit.SECONDS)
        .retry(3)
        .subscribeOn(Schedulers.newThread())
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
  public void action(List<Issue> repos, Callback<List<Issue>> listCallback, boolean firstTime) {
    if (repos != null && repos.size() > 0) {
      listCallback.onResponse(repos, firstTime);
    } else {
      listCallback.onResponseEmpty();
    }
  }

  @NonNull
  @Override
  protected GenericRepository<Void, List<Issue>> configRepository(RestWrapper restWrapper) {
    if (genericRepository == null) {
      genericRepository = new GenericRepository<>(getUserIssuesCacheDataSource(), getCloudIssuesDataSource(restWrapper));
    }
    return genericRepository;
  }

  @NonNull
  @Override
  protected IssuesRetrofitWrapper getRest(ApiClient apiClient, String token) {
    return new IssuesRetrofitWrapper(apiClient, token);
  }

  protected abstract CacheDataSource<Void, List<Issue>> getUserIssuesCacheDataSource();

  protected abstract CloudDataSource<Void, List<Issue>> getCloudIssuesDataSource(RestWrapper issuesRetrofit);
}

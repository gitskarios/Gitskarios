package com.alorma.github.presenter;

import android.support.annotation.NonNull;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.sdk.core.ApiClient;
import com.alorma.github.sdk.core.User;
import com.alorma.github.sdk.core.datasource.CacheDataSource;
import com.alorma.github.sdk.core.datasource.CloudDataSource;
import com.alorma.github.sdk.core.datasource.RestWrapper;
import com.alorma.github.sdk.core.datasource.SdkItem;
import com.alorma.github.sdk.core.orgs.OrganizationsDataSource;
import com.alorma.github.sdk.core.orgs.OrganizationsRetrofitWrapper;
import com.alorma.github.sdk.core.repository.GenericRepository;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@PerActivity public class NavigationProfilesPresenter extends Presenter<String, List<User>> {
  private Integer page;
  private GenericRepository<String, List<User>> genericRepository;

  @Inject
  public NavigationProfilesPresenter() {

  }

  @Override
  public void load(String aString, Callback<List<User>> listCallback) {
    execute(config().execute(new SdkItem<>(aString)), listCallback, true);
  }

  @Override
  public void loadMore(String aString, Callback<List<User>> listCallback) {
    if (page != null) {
      execute(config().execute(new SdkItem<>(page, aString)), listCallback, false);
    }
  }

  private void execute(Observable<SdkItem<List<User>>> observable, Callback<List<User>> listCallback, boolean firstTime) {
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
  public void action(List<User> users, Callback<List<User>> listCallback, boolean firstTime) {
    if (users != null && users.size() > 0) {
      listCallback.onResponse(users, firstTime);
    } else {
      listCallback.onResponseEmpty();
    }
  }

  @NonNull
  @Override
  protected GenericRepository<String, List<User>> configRepository(RestWrapper restWrapper) {
    if (genericRepository == null) {
      genericRepository = new GenericRepository<>(getOrgsCacheDataSource(), getCloudOrgsDataSource(restWrapper));
    }
    return genericRepository;
  }

  @NonNull
  @Override
  protected OrganizationsRetrofitWrapper getRest(ApiClient apiClient, String token) {
    return new OrganizationsRetrofitWrapper(apiClient, token);
  }

  protected CacheDataSource<String, List<User>> getOrgsCacheDataSource() {
    return new CacheDataSource<String, List<User>>() {

      @Override
      public void saveData(SdkItem<String> request, SdkItem<List<User>> data) {

      }

      @Override
      public Observable<SdkItem<List<User>>> getData(SdkItem<String> request) {
        return Observable.empty();
      }
    };
  }

  protected CloudDataSource<String, List<User>> getCloudOrgsDataSource(RestWrapper usersRetrofit) {
    return new OrganizationsDataSource(usersRetrofit);
  }
}

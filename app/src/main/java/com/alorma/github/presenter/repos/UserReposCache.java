package com.alorma.github.presenter.repos;

import com.alorma.github.sdk.core.datasource.CacheDataSource;
import com.alorma.github.sdk.core.datasource.SdkItem;
import com.alorma.github.sdk.core.repositories.Repo;
import java.util.List;
import rx.Observable;

public class UserReposCache implements CacheDataSource<String, List<Repo>> {

  @Override
  public void saveData(SdkItem<String> request, List<Repo> repos) {

  }

  @Override
  public Observable<SdkItem<List<Repo>>> getData(SdkItem<String> request) {
    return Observable.empty();
  }
}

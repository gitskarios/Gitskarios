package com.alorma.github.presenter.repos;

import com.alorma.github.sdk.core.datasource.CacheDataSource;
import com.alorma.github.sdk.core.datasource.SdkResponse;
import com.alorma.github.sdk.core.repositories.Repo;
import java.util.List;
import rx.Observable;

public class UserReposCache implements CacheDataSource<String, List<Repo>> {
  @Override
  public void saveData(String s, List<Repo> repos) {

  }

  @Override
  public Observable<SdkResponse<List<Repo>>> getData(String s) {
    return Observable.empty();
  }
}

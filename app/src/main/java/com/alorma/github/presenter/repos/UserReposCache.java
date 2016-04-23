package com.alorma.github.presenter.repos;

import com.alorma.github.presenter.CacheWrapper;
import com.alorma.github.sdk.core.datasource.CacheDataSource;
import com.alorma.github.sdk.core.datasource.SdkItem;
import com.alorma.github.sdk.core.repositories.Repo;
import com.fewlaps.quitnowcache.QNCache;
import com.fewlaps.quitnowcache.QNCacheBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Observable;

public class UserReposCache implements CacheDataSource<String, List<Repo>> {

  private String key;

  public UserReposCache(String key) {
    this.key = key;
  }

  @Override
  public void saveData(SdkItem<String> request, SdkItem<List<Repo>> repos) {
    String cacheKey = String.format(key, request.getK());
    List<Repo> saveRepos = new ArrayList<>();
    if (CacheWrapper.reposCache().contains(cacheKey)) {
      List<Repo> cachedRepos = CacheWrapper.reposCache().get(cacheKey);
      if (cachedRepos != null) {
        cachedRepos.addAll(repos.getK());
        saveRepos = cachedRepos;
      }
    } else {
      saveRepos = repos.getK();
    }
    CacheWrapper.reposCache().set(cacheKey, saveRepos);
  }

  @Override
  public Observable<SdkItem<List<Repo>>> getData(SdkItem<String> request) {
    String cacheKey = String.format(key, request.getK());
    List<Repo> repos = CacheWrapper.reposCache().get(cacheKey);
    if (repos == null || repos.isEmpty()) {
      return Observable.empty();
    } else {
      return Observable.just(new SdkItem<>(Integer.MIN_VALUE, repos));
    }
  }
}

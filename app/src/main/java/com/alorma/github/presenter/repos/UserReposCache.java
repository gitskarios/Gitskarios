package com.alorma.github.presenter.repos;

import com.alorma.github.presenter.AbstractCacheDataSource;
import com.alorma.github.presenter.CacheWrapper;
import com.alorma.github.sdk.core.datasource.CacheDataSource;
import com.alorma.github.sdk.core.datasource.SdkItem;
import com.alorma.github.sdk.core.repositories.Repo;
import com.alorma.github.sdk.core.repositories.releases.tags.Tag;

import java.util.List;
import java.util.Locale;

import rx.Observable;

public class UserReposCache extends AbstractCacheDataSource<String,List<Repo>> {

  private String key;

  public UserReposCache(String key) {
    this.key = key + "_%s_page_%d";
  }

  @Override
  protected String getCacheKey(String request, Integer page) {
    String cacheKey =
            String.format(Locale.getDefault(), key, request, page);
    return cacheKey;
  }

  @Override
  protected boolean checkItemIsEmpty(SdkItem<List<Repo>> sdkItem) {
    return sdkItem == null || sdkItem.getK() == null || sdkItem.getK().isEmpty();
  }
}

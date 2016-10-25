package com.alorma.github.presenter.repos;

import com.alorma.github.presenter.AbstractCacheDataSource;
import com.alorma.github.sdk.bean.info.RepoInfo;
import core.datasource.SdkItem;
import java.util.Locale;

public class ReadmeCacheDataSource extends AbstractCacheDataSource<RepoInfo, String> {
  private static final String KEY = "repository_readme_%s";

  @Override
  protected String getCacheKey(RepoInfo repoInfo, Integer page) {
    return String.format(Locale.getDefault(), KEY, repoInfo.toString());
  }

  @Override
  protected boolean checkItemIsEmpty(SdkItem<String> sdkItem) {
    return sdkItem == null || sdkItem.getK() == null || sdkItem.getK().isEmpty();
  }
}

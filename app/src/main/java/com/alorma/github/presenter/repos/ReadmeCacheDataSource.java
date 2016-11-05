package com.alorma.github.presenter.repos;

import com.alorma.github.presenter.AbstractCacheDataSource;
import com.alorma.github.sdk.bean.ReadmeInfo;
import core.datasource.SdkItem;
import java.util.Locale;

public class ReadmeCacheDataSource extends AbstractCacheDataSource<ReadmeInfo, String> {
  private static final String KEY = "repository_readme_%s";

  @Override
  protected String getCacheKey(ReadmeInfo readmeInfo, Integer page) {
    return String.format(Locale.getDefault(), KEY, readmeInfo.toString());
  }

  @Override
  protected boolean checkItemIsEmpty(SdkItem<String> sdkItem) {
    return sdkItem == null || sdkItem.getK() == null || sdkItem.getK().isEmpty();
  }
}

package com.alorma.github.presenter.repos.releases.tags;

import com.alorma.github.presenter.AbstractCacheDataSource;
import com.alorma.github.sdk.bean.info.RepoInfo;
import core.datasource.SdkItem;
import core.repositories.releases.tags.Tag;
import java.util.List;
import java.util.Locale;

public class TagsCacheDataSource extends AbstractCacheDataSource<RepoInfo, List<Tag>> {
  private static final String KEY = "repository_tags_%s_page_%d";

  @Override
  protected String getCacheKey(RepoInfo repoInfo, Integer page) {
    return String.format(Locale.getDefault(), KEY, repoInfo.owner + "_" + repoInfo.name, page);
  }

  @Override
  protected boolean checkItemIsEmpty(SdkItem<List<Tag>> sdkItem) {
    return sdkItem == null || sdkItem.getK() == null || sdkItem.getK().isEmpty();
  }
}

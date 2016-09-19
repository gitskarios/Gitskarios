package com.alorma.github.presenter.repos.releases.tags;

import com.alorma.github.presenter.AbstractCacheDataSource;
import com.alorma.github.sdk.core.datasource.SdkItem;
import com.alorma.github.sdk.core.repositories.RepoInfo;
import com.alorma.github.sdk.core.repositories.releases.tags.Tag;

import java.util.List;
import java.util.Locale;

public class TagsCacheDataSource extends AbstractCacheDataSource<RepoInfo, List<Tag>> {
    private static final String KEY = "repository_tags_%s_page_%d";

    @Override
    protected String getCacheKey(RepoInfo repoInfo, Integer page) {
        String cacheKey =
                String.format(Locale.getDefault(), KEY, repoInfo.name, page);
        return cacheKey;
    }

    @Override
    protected boolean checkItemIsEmpty(SdkItem<List<Tag>> sdkItem) {
        return sdkItem == null || sdkItem.getK() == null || sdkItem.getK().isEmpty();
    }
}

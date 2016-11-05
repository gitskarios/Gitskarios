package com.alorma.github.presenter.repo.tags;

import com.alorma.github.sdk.bean.info.RepoInfo;
import core.datasource.SdkItem;
import rx.Observable;

public interface GetTagsCountUseCase {
  Observable<SdkItem<Integer>> getTagsCount(RepoInfo repoInfo);
}

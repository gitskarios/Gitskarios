package com.alorma.github.presenter.repo.tags;

import com.alorma.github.sdk.bean.info.RepoInfo;
import core.datasource.SdkItem;
import core.repository.GenericRepository;
import rx.Observable;

public class GetTagsCountUseCaseImpl implements GetTagsCountUseCase {
  private final GenericRepository<RepoInfo, Integer> genericRepository;

  public GetTagsCountUseCaseImpl(GenericRepository<RepoInfo, Integer> genericRepository) {

    this.genericRepository = genericRepository;
  }

  @Override
  public Observable<SdkItem<Integer>> getTagsCount(RepoInfo repoInfo) {
    return genericRepository.execute(new SdkItem<>(repoInfo));
  }
}

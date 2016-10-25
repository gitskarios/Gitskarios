package com.alorma.github.presenter;

import com.alorma.github.sdk.bean.info.RepoInfo;
import core.repository.GenericRepository;
import rx.Scheduler;

public class RepositoryReadmePresenter extends BaseRxPresenter<RepoInfo, String, View<String>> {

  public RepositoryReadmePresenter(Scheduler mainScheduler, Scheduler ioScheduler, GenericRepository<RepoInfo, String> readmeRepository) {
    super(mainScheduler, ioScheduler, readmeRepository);
  }
}
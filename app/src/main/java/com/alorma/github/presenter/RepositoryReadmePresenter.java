package com.alorma.github.presenter;

import com.alorma.github.sdk.bean.ReadmeInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import core.repository.GenericRepository;
import rx.Scheduler;

public class RepositoryReadmePresenter extends BaseRxPresenter<ReadmeInfo, String, View<String>> {

  public RepositoryReadmePresenter(Scheduler mainScheduler, Scheduler ioScheduler, GenericRepository<ReadmeInfo, String> readmeRepository) {
    super(mainScheduler, ioScheduler, readmeRepository);
  }
}
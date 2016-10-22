package com.alorma.github.presenter.repos;

import com.alorma.github.presenter.BaseRxPresenter;
import com.alorma.github.presenter.View;
import core.repositories.Repo;
import core.repository.GenericRepository;
import java.util.List;
import rx.Scheduler;

public class AuthWatchedRepositoriesPresenter extends BaseRxPresenter<String, List<Repo>, View<List<Repo>>> {

  public AuthWatchedRepositoriesPresenter(Scheduler mainScheduler, Scheduler ioScheduler,
      GenericRepository<String, List<Repo>> repository) {
    super(mainScheduler, ioScheduler, repository);
  }
}

package com.alorma.github.presenter;

import core.User;
import core.repository.GenericRepository;
import java.util.List;
import rx.Scheduler;

public class NavigationProfilesPresenter extends BaseRxPresenter<String, List<User>, View<List<User>>> {

  public NavigationProfilesPresenter(Scheduler mainScheduler, Scheduler ioScheduler, GenericRepository<String, List<User>> repository) {
    super(mainScheduler, ioScheduler, repository);
  }
}

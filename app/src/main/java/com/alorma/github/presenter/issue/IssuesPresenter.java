package com.alorma.github.presenter.issue;

import com.alorma.github.presenter.BaseRxPresenter;
import com.alorma.github.presenter.View;
import core.search.IssuesSearchRequest;
import core.issues.Issue;
import core.repository.GenericRepository;
import java.util.List;
import rx.Scheduler;

public class IssuesPresenter extends BaseRxPresenter<IssuesSearchRequest, List<Issue>, View<List<Issue>>> {

  public IssuesPresenter(Scheduler mainScheduler, Scheduler ioScheduler,
      GenericRepository<IssuesSearchRequest, List<Issue>> repository) {
    super(mainScheduler, ioScheduler, repository);
  }
}

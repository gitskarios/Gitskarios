package com.alorma.github.presenter.issue;

import com.alorma.github.presenter.BaseRxPresenter;
import com.alorma.github.presenter.View;
import core.issue.IssuesSearchRequest;
import core.issues.Issue;
import core.repository.GenericRepository;
import java.util.List;
import rx.Scheduler;

public class UserIssuesBaseRxPresenter extends BaseRxPresenter<IssuesSearchRequest, List<Issue>, View<List<Issue>>> {

  public UserIssuesBaseRxPresenter(Scheduler mainScheduler, Scheduler ioScheduler,
      GenericRepository<IssuesSearchRequest, List<Issue>> repository) {
    super(mainScheduler, ioScheduler, repository);
  }
}

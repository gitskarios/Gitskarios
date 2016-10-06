package com.alorma.github.presenter.issue;

import com.alorma.github.presenter.BaseRxPresenter;
import com.alorma.github.presenter.View;

import core.GithubComment;
import core.issue.EditIssueCommentBodyRequest;
import core.repository.GenericRepository;
import rx.Scheduler;

public class IssueCommentBaseRxPresenter
        extends BaseRxPresenter<EditIssueCommentBodyRequest, GithubComment, View<GithubComment>> {

  public IssueCommentBaseRxPresenter(Scheduler mainScheduler, Scheduler ioScheduler,
          GenericRepository<EditIssueCommentBodyRequest, GithubComment> genericRepository) {
    super(mainScheduler, ioScheduler, genericRepository);
  }
}

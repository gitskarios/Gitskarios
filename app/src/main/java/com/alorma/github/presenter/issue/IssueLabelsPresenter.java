package com.alorma.github.presenter.issue;

import com.alorma.github.presenter.BaseRxPresenter;
import com.alorma.github.presenter.View;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.services.issues.GithubIssueLabelsClient;
import core.ApiClient;
import core.datasource.RestWrapper;
import core.issues.Label;
import core.repository.GenericRepository;
import java.util.List;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class IssueLabelsPresenter
        extends BaseRxPresenter<IssueInfo, List<Label>, View<List<Label>>> {

  public IssueLabelsPresenter(Scheduler mainScheduler, Scheduler ioScheduler) {
    super(mainScheduler, ioScheduler, null);
  }

  @Override
  public void execute(IssueInfo issueInfo) {
    if(!isViewAttached()) return;

    GithubIssueLabelsClient labelsClient = new GithubIssueLabelsClient(issueInfo.repoInfo, true);
    labelsClient.observable()
            .subscribeOn(ioScheduler)
            .observeOn(mainScheduler)
            .doOnSubscribe(() -> getView().showLoading())
            .doOnCompleted(() -> getView().hideLoading())
            .subscribe(labels -> getView().onDataReceived(labels, false),
                    throwable -> getView().showError(throwable));
  }
}

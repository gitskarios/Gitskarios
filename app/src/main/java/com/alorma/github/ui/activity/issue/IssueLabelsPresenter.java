package com.alorma.github.ui.activity.issue;

import com.alorma.github.presenter.Presenter;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.services.issues.GithubIssueLabelsClient;
import core.ApiClient;
import core.datasource.RestWrapper;
import core.issues.Label;
import core.repository.GenericRepository;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class IssueLabelsPresenter extends Presenter<IssueInfo, List<Label>> {
  @Override
  public void load(IssueInfo issueInfo, Callback<List<Label>> listCallback) {
    GithubIssueLabelsClient labelsClient = new GithubIssueLabelsClient(issueInfo.repoInfo, true);
    labelsClient.observable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe(listCallback::showLoading)
        .doOnCompleted(listCallback::hideLoading)
        .subscribe(labels -> action(labels, listCallback, true), Throwable::printStackTrace);
  }

  @Override
  public void loadMore(IssueInfo issueInfo, Callback<List<Label>> listCallback) {

  }

  @Override
  protected GenericRepository<IssueInfo, List<Label>> configRepository(RestWrapper restWrapper) {
    return null;
  }

  @Override
  protected RestWrapper getRest(ApiClient apiClient, String token) {
    return null;
  }

  @Override
  public void action(List<Label> labels, Callback<List<Label>> listCallback, boolean firstTime) {
    listCallback.onResponse(labels, firstTime);
  }
}

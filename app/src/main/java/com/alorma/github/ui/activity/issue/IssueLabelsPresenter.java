package com.alorma.github.ui.activity.issue;

import com.alorma.github.presenter.Presenter;
import com.alorma.github.sdk.bean.dto.response.Label;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.services.issues.GithubIssueLabelsClient;
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
        .subscribe(listCallback::onResponse, Throwable::printStackTrace);
  }
}

package com.alorma.github.ui.activity.issue;

import com.alorma.github.presenter.Presenter;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.core.ApiClient;
import com.alorma.github.sdk.core.datasource.RestWrapper;
import com.alorma.github.sdk.core.repository.GenericRepository;
import com.alorma.github.sdk.services.repo.GetRepoCollaboratorsClient;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class IssueAssigneesPresenter extends Presenter<IssueInfo, List<User>> {
  @Override
  public void load(IssueInfo issueInfo, Callback<List<User>> listCallback) {
    GetRepoCollaboratorsClient contributorsClient =
        new GetRepoCollaboratorsClient(issueInfo.repoInfo);
    contributorsClient.observable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe(listCallback::showLoading)
        .doOnCompleted(listCallback::hideLoading)
        .subscribe(listCallback::onResponse, Throwable::printStackTrace);
  }

  @Override
  public void loadMore(IssueInfo issueInfo, Callback<List<User>> listCallback) {

  }

  @Override
  protected GenericRepository<IssueInfo, List<User>> configRepository(RestWrapper restWrapper) {
    return null;
  }

  @Override
  protected RestWrapper getRest(ApiClient apiClient, String token) {
    return null;
  }

  @Override
  protected ApiClient getApiClient() {
    return null;
  }

  @Override
  public void action(List<User> users, Callback<List<User>> listCallback) {

  }
}

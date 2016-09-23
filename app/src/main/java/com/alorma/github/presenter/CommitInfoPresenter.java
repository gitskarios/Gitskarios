package com.alorma.github.presenter;

import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.sdk.bean.dto.response.Commit;
import com.alorma.github.sdk.bean.dto.response.GithubStatusResponse;
import com.alorma.github.sdk.bean.info.CommitInfo;
import com.alorma.github.sdk.services.commit.GetSingleCommitClient;
import com.alorma.github.sdk.services.repo.GetShaCombinedStatus;
import core.ApiClient;
import core.datasource.RestWrapper;
import core.repository.GenericRepository;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@PerActivity public class CommitInfoPresenter extends Presenter<CommitInfo, Commit> {

  @Inject
  public CommitInfoPresenter() {

  }

  @Override
  public void load(CommitInfo commitInfo, Callback<Commit> commitCallback) {
    Observable<Commit> singleCommitClient = new GetSingleCommitClient(commitInfo).observable();
    Observable<GithubStatusResponse> shaCombinedStatus = new GetShaCombinedStatus(commitInfo.repoInfo, commitInfo.sha).observable()
        .onErrorResumeNext(throwable -> Observable.empty())
        .map(o -> o.first);

    Observable<Commit> zip = Observable.zip(singleCommitClient, shaCombinedStatus, (commit, status) -> {
      commit.combinedStatus = status;
      return commit;
    });

    zip.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe(commitCallback::showLoading)
        .subscribe(commit -> {
          action(commit, commitCallback, true);
        }, Throwable::printStackTrace);
  }

  @Override
  public void loadMore(CommitInfo commitInfo, Callback<Commit> commitCallback) {

  }

  @Override
  protected GenericRepository<CommitInfo, Commit> configRepository(RestWrapper restWrapper) {
    return null;
  }

  @Override
  protected RestWrapper getRest(ApiClient apiClient, String token) {
    return null;
  }

  @Override
  public void action(Commit commit, Callback<Commit> commitCallback, boolean firstTime) {
    commitCallback.hideLoading();
    commitCallback.onResponse(commit, firstTime);
  }
}

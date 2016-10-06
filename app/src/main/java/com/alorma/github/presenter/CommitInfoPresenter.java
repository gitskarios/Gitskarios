package com.alorma.github.presenter;

import com.alorma.github.sdk.bean.dto.response.Commit;
import com.alorma.github.sdk.bean.dto.response.GithubStatusResponse;
import com.alorma.github.sdk.bean.info.CommitInfo;
import com.alorma.github.sdk.services.commit.GetSingleCommitClient;
import com.alorma.github.sdk.services.repo.GetShaCombinedStatus;

import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CommitInfoPresenter extends BaseRxPresenter<CommitInfo, Commit, View<Commit>> {

  public CommitInfoPresenter(Scheduler mainScheduler, Scheduler ioScheduler) {
    super(mainScheduler, ioScheduler, null);
  }

  @Override
  public void execute(CommitInfo commitInfo) {
    if(!isViewAttached()) return;

    Observable<Commit> singleCommitClient = new GetSingleCommitClient(commitInfo).observable();
    Observable<GithubStatusResponse> shaCombinedStatus =
            new GetShaCombinedStatus(commitInfo.repoInfo, commitInfo.sha).observable()
        .onErrorResumeNext(throwable -> Observable.empty())
        .map(o -> o.first);

    Observable<Commit> zip = Observable.zip(singleCommitClient, shaCombinedStatus, (commit, status) -> {
      commit.combinedStatus = status;
      return commit;
    });

    zip.subscribeOn(ioScheduler)
            .observeOn(mainScheduler)
            .doOnSubscribe(() -> getView().showLoading())
            .subscribe(commit -> {
                        getView().onDataReceived(commit, false);
                        getView().hideLoading();
                      },
                      throwable -> {
                        getView().showError(throwable);
                        getView().hideLoading();
                      });
  }
}

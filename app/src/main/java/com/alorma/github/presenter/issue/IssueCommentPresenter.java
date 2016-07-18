package com.alorma.github.presenter.issue;

import com.alorma.github.injector.PerActivity;
import com.alorma.github.presenter.Presenter;
import com.alorma.github.sdk.core.ApiClient;
import com.alorma.github.sdk.core.GithubComment;
import com.alorma.github.sdk.core.datasource.CloudDataSource;
import com.alorma.github.sdk.core.datasource.RestWrapper;
import com.alorma.github.sdk.core.datasource.SdkItem;
import com.alorma.github.sdk.core.issue.EditIssueCommentBodyRequest;
import com.alorma.github.sdk.core.issue.IssuesRetrofitWrapper;
import com.alorma.github.sdk.core.repository.GenericRepository;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@PerActivity public class IssueCommentPresenter extends Presenter<EditIssueCommentBodyRequest, GithubComment> {

  @Inject
  public IssueCommentPresenter() {
  }

  @Override
  public void load(EditIssueCommentBodyRequest s, Callback<GithubComment> githubCommentCallback) {
    execute(config().execute(new SdkItem<>(s)), githubCommentCallback);
  }

  private void execute(Observable<SdkItem<GithubComment>> observable, Callback<GithubComment> githubCommentCallback) {
    observable.subscribeOn(Schedulers.io())
        .map(SdkItem::getK)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe(githubCommentCallback::showLoading)
        .doOnCompleted(githubCommentCallback::hideLoading)
        .subscribe(repos -> action(repos, githubCommentCallback, true), throwable -> {
          githubCommentCallback.onResponseEmpty();
          githubCommentCallback.hideLoading();
          throwable.printStackTrace();
        });
  }

  @Override
  public void loadMore(EditIssueCommentBodyRequest s, Callback<GithubComment> githubCommentCallback) {

  }

  @Override
  protected GenericRepository<EditIssueCommentBodyRequest, GithubComment> configRepository(RestWrapper restWrapper) {
    CloudDataSource<EditIssueCommentBodyRequest, GithubComment> api = new EditIssueCommentDataSource(restWrapper);
    return new GenericRepository<>(null, api);
  }

  @Override
  protected RestWrapper getRest(ApiClient apiClient, String token) {
    return new IssuesRetrofitWrapper(apiClient, token);
  }

  @Override
  public void action(GithubComment githubComment, Callback<GithubComment> githubCommentCallback, boolean firstTime) {
    githubCommentCallback.hideLoading();
    githubCommentCallback.onResponse(githubComment, firstTime);
  }
}

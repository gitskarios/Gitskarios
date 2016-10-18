package com.alorma.github.ui.actions;

import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.services.issues.NewIssueCommentClient;
import core.GithubComment;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AddIssueCommentAction extends Action<GithubComment> {

  private IssueInfo issueInfo;
  private String body;
  private AddCommentCallback addCommentCallback;

  public AddIssueCommentAction(IssueInfo issueInfo, String body) {
    this.issueInfo = issueInfo;

    this.body = body;
  }

  @Override
  public Action<GithubComment> execute() {
    if (addCommentCallback != null) {
      addCommentCallback.onCommentAddStarted();
    }
    NewIssueCommentClient client = new NewIssueCommentClient(issueInfo, body);
    client.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this);
    return this;
  }

  @Override
  public void onError(Throwable e) {
    if (addCommentCallback != null) {
      addCommentCallback.onCommentError();
    }
  }

  @Override
  public void onNext(GithubComment githubComment) {
    if (addCommentCallback != null) {
      addCommentCallback.onCommentAdded();
    }
  }

  public void setAddCommentCallback(AddCommentCallback addCommentCallback) {
    this.addCommentCallback = addCommentCallback;
  }

  public interface AddCommentCallback {
    void onCommentAdded();

    void onCommentError();

    void onCommentAddStarted();
  }
}

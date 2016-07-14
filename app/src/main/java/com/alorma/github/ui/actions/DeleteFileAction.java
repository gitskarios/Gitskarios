package com.alorma.github.ui.actions;

import com.alorma.github.sdk.bean.dto.request.NewContentRequest;
import com.alorma.github.sdk.bean.dto.response.Content;
import com.alorma.github.sdk.bean.dto.response.NewContentResponse;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.content.DeleteFileClient;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DeleteFileAction extends Action<NewContentResponse> {
  private Content content;
  private RepoInfo repoInfo;
  private String message;

  public DeleteFileAction(Content content, RepoInfo repoInfo, String message) {
    this.content = content;
    this.repoInfo = repoInfo;
    this.message = message;
  }

  @Override
  public Action<NewContentResponse> execute() {
    NewContentRequest request = new NewContentRequest();
    request.sha = content.sha;
    request.message = message;
    new DeleteFileClient(request, repoInfo, content.path).observable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(newContentResponse -> {

        }, throwable -> {

        });
    return this;
  }
}

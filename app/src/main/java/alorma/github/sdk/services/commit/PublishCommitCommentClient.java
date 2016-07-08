package com.alorma.github.sdk.services.commit;

import com.alorma.github.sdk.bean.dto.request.CommitCommentRequest;
import com.alorma.github.sdk.bean.dto.response.CommitComment;
import com.alorma.github.sdk.bean.info.CommitInfo;
import com.alorma.github.sdk.services.client.GithubClient;
import retrofit.RestAdapter;
import rx.Observable;

public class PublishCommitCommentClient extends GithubClient<CommitComment> {

  private CommitInfo info;
  private CommitCommentRequest request;

  public PublishCommitCommentClient(CommitInfo info, CommitCommentRequest request) {
    super();
    this.info = info;
    this.request = request;
  }

  @Override
  protected Observable<CommitComment> getApiObservable(RestAdapter restAdapter) {
    return restAdapter.create(CommitsService.class)
        .publishComment(info.repoInfo.owner, info.repoInfo.name, info.sha, request);
  }
}

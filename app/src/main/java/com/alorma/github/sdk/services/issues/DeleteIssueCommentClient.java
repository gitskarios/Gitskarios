package com.alorma.github.sdk.services.issues;

import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.GithubClient;
import retrofit.RestAdapter;
import retrofit.client.Response;
import rx.Observable;

public class DeleteIssueCommentClient extends GithubClient<Response> {

  private RepoInfo info;
  private String id;

  public DeleteIssueCommentClient(RepoInfo info, String id) {
    super();
    this.info = info;
    this.id = id;
  }

  @Override
  protected Observable<Response> getApiObservable(RestAdapter restAdapter) {
    return restAdapter.create(IssuesService.class).deleteComment(info.owner, info.name, id);
  }
}

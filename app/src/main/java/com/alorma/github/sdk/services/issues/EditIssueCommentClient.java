package com.alorma.github.sdk.services.issues;

import com.alorma.github.sdk.bean.dto.request.CommentRequest;
import com.alorma.github.sdk.bean.dto.response.GithubComment;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.GithubClient;
import retrofit.RestAdapter;
import rx.Observable;

public class EditIssueCommentClient extends GithubClient<GithubComment> {

  private RepoInfo info;
  private String id;
  private CommentRequest body;

  public EditIssueCommentClient(RepoInfo info, String id, CommentRequest body) {
    super();
    this.info = info;
    this.id = id;
    this.body = body;
  }

  @Override
  protected Observable<GithubComment> getApiObservable(RestAdapter restAdapter) {
    return restAdapter.create(IssuesService.class).editComment(info.owner, info.name, id, body);
  }
}

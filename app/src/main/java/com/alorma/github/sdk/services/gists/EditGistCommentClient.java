package com.alorma.github.sdk.services.gists;

import com.alorma.github.sdk.bean.dto.request.CommentRequest;
import com.alorma.github.sdk.bean.dto.response.GithubComment;
import com.alorma.github.sdk.services.client.GithubClient;
import retrofit.RestAdapter;
import rx.Observable;

public class EditGistCommentClient extends GithubClient<GithubComment> {

  private String gistId;
  private CommentRequest body;
  private String commentId;

  public EditGistCommentClient(String gistId, String commentId, CommentRequest body) {
    super();
    this.gistId = gistId;
    this.body = body;
    this.commentId = commentId;
  }

  @Override
  protected Observable<GithubComment> getApiObservable(RestAdapter restAdapter) {
    return restAdapter.create(GistsService.class).editComment(gistId, commentId, body);
  }
}

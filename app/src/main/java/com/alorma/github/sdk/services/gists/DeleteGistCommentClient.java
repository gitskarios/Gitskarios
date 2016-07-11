package com.alorma.github.sdk.services.gists;

import com.alorma.github.sdk.services.client.GithubClient;
import retrofit.RestAdapter;
import retrofit.client.Response;
import rx.Observable;

public class DeleteGistCommentClient extends GithubClient<Response> {

  private String id;
  private String commentId;

  public DeleteGistCommentClient(String commentId, String id) {
    super();
    this.commentId = commentId;
    this.id = id;
  }

  @Override
  protected Observable<Response> getApiObservable(RestAdapter restAdapter) {
    return restAdapter.create(GistsService.class).deleteComment(id, commentId);
  }
}

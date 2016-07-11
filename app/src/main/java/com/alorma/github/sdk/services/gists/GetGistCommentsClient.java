package com.alorma.github.sdk.services.gists;

import com.alorma.github.sdk.bean.dto.response.GithubComment;
import com.alorma.github.sdk.services.client.GithubListClient;
import java.util.List;
import retrofit.RestAdapter;

public class GetGistCommentsClient extends GithubListClient<List<GithubComment>> {

  private String id;
  private int page;

  public GetGistCommentsClient(String id) {
    this(id, 0);
  }

  public GetGistCommentsClient(String id, int page) {
    super();
    this.id = id;
    this.page = page;
  }

  @Override
  protected ApiSubscriber getApiObservable(RestAdapter restAdapter) {
    return new ApiSubscriber() {
      @Override
      protected void call(RestAdapter restAdapter) {
        GistsService gistsService = restAdapter.create(GistsService.class);

        if (page == 0) {
          gistsService.comments(id, this);
        } else {
          gistsService.comments(id, page, this);
        }
      }
    };
  }
}

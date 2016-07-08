package com.alorma.github.sdk.services.gists;

import com.alorma.github.sdk.bean.dto.response.Gist;
import com.alorma.github.sdk.services.client.GithubListClient;
import java.util.List;
import retrofit.RestAdapter;

public class PublicGistsClient extends GithubListClient<List<Gist>> {
  private int page = 0;

  public PublicGistsClient() {
    super();
  }

  public PublicGistsClient(int page) {
    super();
    this.page = page;
  }

  @Override
  protected ApiSubscriber getApiObservable(RestAdapter restAdapter) {
    return new ApiSubscriber() {
      @Override
      protected void call(RestAdapter restAdapter) {
        GistsService gistsService = restAdapter.create(GistsService.class);
        if (page == 0) {
          gistsService.publicGistsList(this);
        } else {
          gistsService.publicGistsList(page, this);
        }
      }
    };
  }
}

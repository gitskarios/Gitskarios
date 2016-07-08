package com.alorma.github.sdk.services.gists;

import com.alorma.github.sdk.bean.dto.response.Gist;
import com.alorma.github.sdk.services.client.GithubListClient;
import java.util.List;
import retrofit.RestAdapter;

public class UserGistsClient extends GithubListClient<List<Gist>> {

  private String username;
  private int page = 0;

  public UserGistsClient() {
    super();
  }

  public UserGistsClient(int page) {
    super();
    this.page = page;
  }

  public UserGistsClient(String username) {
    super();
    this.username = username;
  }

  public UserGistsClient(String username, int page) {
    super();
    this.username = username;
    this.page = page;
  }

  @Override
  protected ApiSubscriber getApiObservable(RestAdapter restAdapter) {
    return new ApiSubscriber() {
      @Override
      protected void call(RestAdapter restAdapter) {
        GistsService gistsService = restAdapter.create(GistsService.class);
        if (page == 0) {
          if (username == null) {
            gistsService.userGistsListAsync(this);
          } else {
            gistsService.userGistsListAsync(username, this);
          }
        } else {
          if (username == null) {
            gistsService.userGistsListAsync(page, this);
          } else {
            gistsService.userGistsListAsync(username, page, this);
          }
        }
      }
    };
  }

  @Override
  public String getAcceptHeader() {
    return "application/vnd.github.v3.raw";
  }
}

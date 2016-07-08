package com.alorma.github.sdk.services.user;

import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.services.client.GithubListClient;
import java.util.List;
import retrofit.RestAdapter;

/**
 * Created by Bernat on 14/07/2014.
 */
public class UserFollowingClient extends GithubListClient<List<User>> {

  private String username;
  private int page = 0;

  public UserFollowingClient(String username) {
    super();
    this.username = username;
  }

  public UserFollowingClient(String username, int page) {
    super();
    this.username = username;
    this.page = page;
  }

  @Override
  protected ApiSubscriber getApiObservable(RestAdapter restAdapter) {
    return new ApiSubscriber() {
      @Override
      protected void call(RestAdapter restAdapter) {
        UsersService usersService = restAdapter.create(UsersService.class);
        if (page == 0) {
          if (username == null) {
            usersService.following(this);
          } else {
            usersService.following(username, this);
          }
        } else {
          if (username == null) {
            usersService.following(page, this);
          } else {
            usersService.following(username, page, this);
          }
        }
      }
    };
  }
}

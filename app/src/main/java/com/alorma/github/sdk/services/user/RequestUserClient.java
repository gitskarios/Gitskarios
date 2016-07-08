package com.alorma.github.sdk.services.user;

import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.services.client.GithubClient;
import retrofit.RestAdapter;
import rx.Observable;

/**
 * Created by Bernat on 12/07/2014.
 */
public class RequestUserClient extends GithubClient<User> {

  private String username;

  public RequestUserClient(String username) {
    super();
    this.username = username;
  }

  @Override
  protected Observable<User> getApiObservable(RestAdapter restAdapter) {
    return restAdapter.create(UsersService.class).getSingleUser(username);
  }
}

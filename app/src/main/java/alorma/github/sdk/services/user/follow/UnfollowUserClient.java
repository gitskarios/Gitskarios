package com.alorma.github.sdk.services.user.follow;

import com.alorma.github.sdk.services.client.GithubClient;
import com.alorma.github.sdk.services.user.UsersService;
import retrofit.RestAdapter;
import retrofit.client.Response;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Bernat on 27/12/2014.
 */
public class UnfollowUserClient extends GithubClient<Boolean> {

  private String username;

  public UnfollowUserClient(String username) {
    super();
    this.username = username;
  }

  @Override
  protected Observable<Boolean> getApiObservable(RestAdapter restAdapter) {
    return restAdapter.create(UsersService.class)
        .unfollowUser(username)
        .map(new Func1<Response, Boolean>() {
          @Override
          public Boolean call(Response r) {
            return !(r != null && r.getStatus() == 204);
          }
        });
  }

  @Override
  public void intercept(RequestFacade request) {
    super.intercept(request);
    request.addHeader("Content-Length", "0");
  }
}

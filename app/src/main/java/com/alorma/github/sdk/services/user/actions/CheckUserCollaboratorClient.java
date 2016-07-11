package com.alorma.github.sdk.services.user.actions;

import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.GithubClient;
import retrofit.RestAdapter;
import retrofit.client.Response;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Bernat on 07/08/2014.
 */
public class CheckUserCollaboratorClient extends GithubClient<Boolean> {
  private RepoInfo info;
  private String user;

  public CheckUserCollaboratorClient(RepoInfo info, String user) {
    super();
    this.info = info;
    this.user = user;
  }

  @Override
  protected Observable<Boolean> getApiObservable(RestAdapter restAdapter) {
    return restAdapter.create(UserActionsService.class)
        .checkIfUserIsCollaborator(info.owner, info.name, user)
        .map(new Func1<Response, Boolean>() {
          @Override
          public Boolean call(Response r) {
            return r != null && r.getStatus() == 204;
          }
        });
  }
}

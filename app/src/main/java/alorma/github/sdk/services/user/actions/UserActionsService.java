package com.alorma.github.sdk.services.user.actions;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

public interface UserActionsService {

  //Sync
  @GET("/repos/{owner}/{name}/collaborators/{username}")
  Observable<Response> checkIfUserIsCollaborator(@Path("owner") String owner,
      @Path("name") String repo, @Path("username") String username);
}

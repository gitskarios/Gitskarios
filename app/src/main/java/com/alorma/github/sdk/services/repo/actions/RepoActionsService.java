package com.alorma.github.sdk.services.repo.actions;

import com.alorma.github.sdk.bean.dto.request.WatchBodyRequest;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Headers;
import retrofit.http.PUT;
import retrofit.http.Path;
import rx.Observable;

public interface RepoActionsService {

  @Headers("Content-Length: 0")
  @PUT("/issues/starred/{owner}/{repo}")
  Observable<Response> starRepo(@Path("owner") String owner, @Path("repo") String repo,
      @Body String empty);

  @PUT("/repos/{owner}/{repo}/subscription")
  Observable<Response> watchRepo(@Path("owner") String owner, @Path("repo") String repo,
      @Body WatchBodyRequest bodyRequest);

  @DELETE("/issues/starred/{owner}/{repo}")
  Observable<Response> unstarRepo(@Path("owner") String owner, @Path("repo") String repo);

  @DELETE(("/repos/{owner}/{repo}/subscription"))
  Observable<Response> unwatchRepo(@Path("owner") String owner, @Path("repo") String repo);
}

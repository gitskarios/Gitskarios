package com.alorma.github.sdk.services.repo.actions;

import com.alorma.github.sdk.bean.dto.request.WatchBodyRequest;
import com.alorma.github.sdk.bean.dto.response.Repo;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by Bernat on 07/08/2014.
 */
public interface RepoActionsService {

  @Headers("Content-Length: 0")
  @PUT("/user/starred/{owner}/{repo}")
  Observable<Response> starRepo(@Path("owner") String owner, @Path("repo") String repo,
      @Body String empty);

  @PUT("/repos/{owner}/{repo}/subscription")
  Observable<Response> watchRepo(@Path("owner") String owner, @Path("repo") String repo,
      @Body WatchBodyRequest bodyRequest);

  @GET("/user/starred/{owner}/{repo}")
  Observable<Response> checkIfRepoIsStarred(@Path("owner") String owner, @Path("repo") String repo);

  @DELETE("/user/starred/{owner}/{repo}")
  Observable<Response> unstarRepo(@Path("owner") String owner, @Path("repo") String repo);

  @GET("/repos/{owner}/{repo}/subscription")
  Observable<Response> checkIfRepoIsWatched(@Path("owner") String owner, @Path("repo") String repo);

  @DELETE(("/repos/{owner}/{repo}/subscription"))
  Observable<Response> unwatchRepo(@Path("owner") String owner, @Path("repo") String repo);

  @Headers("Content-Length: 0")
  @POST("/repos/{owner}/{repo}/forks")
  Observable<Repo> forkRepo(@Path("owner") String owner, @Path("repo") String repo,
      @Body Object empty);

  @Headers("Content-Length: 0")
  @POST("/repos/{owner}/{repo}/forks")
  Observable<Repo> forkRepo(@Path("owner") String owner, @Path("repo") String repo,
      @Query("organization") String org, @Body Object empty);
}

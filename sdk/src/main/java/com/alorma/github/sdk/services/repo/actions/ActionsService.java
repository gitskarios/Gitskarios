package com.alorma.github.sdk.services.repo.actions;

import retrofit.Callback;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.PUT;
import retrofit.http.Path;

/**
 * Created by Bernat on 07/08/2014.
 */
public interface ActionsService {

    @GET("/user/starred/{owner}/{repo}")
    void checkIfRepoIsStarred(@Path("owner") String owner, @Path("repo") String repo, Callback<Object> callback);

    @Headers("Content-Length: 0")
    @PUT("/user/starred/{owner}/{repo}")
    void starRepo(@Path("owner") String owner, @Path("repo") String repo, Callback<Object> callback);

    @DELETE("/user/starred/{owner}/{repo}")
    void unstarRepo(@Path("owner") String owner, @Path("repo") String repo, Callback<Object> callback);

    @GET("/user/subscriptions/{owner}/{repo}")
    void checkIfRepoIsWatched(@Path("owner") String owner, @Path("repo") String repo, Callback<Object> callback);

    @Headers("Content-Length: 0")
    @PUT("/user/subscriptions/{owner}/{repo}")
    void watchRepo(@Path("owner") String owner, @Path("repo") String repo, Callback<Object> callback);

    @DELETE("/user/subscriptions/{owner}/{repo}")
    void unwatchRepo(@Path("owner") String owner, @Path("repo") String repo, Callback<Object> callback);

}

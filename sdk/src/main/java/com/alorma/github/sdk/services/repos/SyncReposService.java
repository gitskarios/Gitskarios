package com.alorma.github.sdk.services.repos;

import com.alorma.github.sdk.bean.dto.response.ListRepos;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface SyncReposService {

    @GET("/user/repos?type=all")
    Response userReposList(@Query("page") int page);

    @GET("/users/{username}/repos?type=all")
    Response userReposList(@Path("username") String username, @Query("page") int page);
}
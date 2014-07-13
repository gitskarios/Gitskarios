package com.alorma.github.sdk.services.repos;

import com.alorma.github.sdk.bean.dto.response.ListGists;
import com.alorma.github.sdk.bean.dto.response.ListRepos;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Bernat on 08/07/2014.
 */
public interface ReposService {

    @GET("/user/repos?type=all")
    void userReposList(Callback<ListRepos> callback);

    @GET("/user/repos?type=all")
    void userReposList(@Query("page") int page, Callback<ListRepos> callback);

    @GET("/users/{username}/repos?type=all")
    void userReposList(@Path("username") String username, Callback<ListRepos> callback);

    @GET("/users/{username}/repos?type=all")
    void userReposList(@Path("username") String username, @Query("page") int page, Callback<ListRepos> callback);

}

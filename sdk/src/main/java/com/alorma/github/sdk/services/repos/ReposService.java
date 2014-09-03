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

    // User repositories
    @GET("/user/repos?type=all")
    void userReposList(Callback<ListRepos> callback);

    @GET("/user/repos?type=all")
    void userReposList(@Query("page") int page, Callback<ListRepos> callback);

    @GET("/users/{username}/repos?type=all")
    void userReposList(@Path("username") String username, Callback<ListRepos> callback);

    @GET("/users/{username}/repos?type=all")
    void userReposList(@Path("username") String username, @Query("page") int page, Callback<ListRepos> callback);

    @GET("/orgs/{org}/repos?type=all")
    void orgsReposList(@Path("org") String org, Callback<ListRepos> callback);

    @GET("/orgs/{org}/repos?type=all")
    void orgsReposList(@Path("org") String org, @Query("page") int page, Callback<ListRepos> callback);

    // Starred repos
    @GET("/user/starred?sort=updated")
    void userStarredReposList(Callback<ListRepos> callback);

    @GET("/user/starred?sort=updated")
    void userStarredReposList(@Query("page") int page, Callback<ListRepos> callback);

    @GET("/users/{username}/starred?sort=updated")
    void userStarredReposList(@Path("username") String username, Callback<ListRepos> callback);

    @GET("/users/{username}/starred?sort=updated")
    void userStarredReposList(@Path("username") String username, @Query("page") int page, Callback<ListRepos> callback);

    // Wathched repos
    @GET("/user/subscriptions")
    void userSubscribedReposList(Callback<ListRepos> callback);

    @GET("/user/subscriptions")
    void userSubscribedReposList(@Query("page") int page, Callback<ListRepos> callback);

    @GET("/users/{username}/subscriptions")
    void userSubscribedReposList(@Path("username") String username, Callback<ListRepos> callback);

    @GET("/users/{username}/subscriptions")
    void userSubscribedReposList(@Path("username") String username, @Query("page") int page, Callback<ListRepos> callback);


}

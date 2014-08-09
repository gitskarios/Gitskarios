package com.alorma.github.sdk.services.repo;

import com.alorma.github.sdk.bean.dto.response.Content;
import com.alorma.github.sdk.bean.dto.response.ListBranches;
import com.alorma.github.sdk.bean.dto.response.ListContents;
import com.alorma.github.sdk.bean.dto.response.ListContributors;
import com.alorma.github.sdk.bean.dto.response.ListIssues;
import com.alorma.github.sdk.bean.dto.response.ListReleases;
import com.alorma.github.sdk.bean.dto.response.Repo;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Bernat on 17/07/2014.
 */
public interface RepoService {

    @GET("/repos/{owner}/{repo}")
    void get(@Path("owner") String owner, @Path("repo") String repo, Callback<Repo> callback);

    @GET("/repos/{owner}/{repo}/branches")
    void branches(@Path("owner") String owner, @Path("repo") String repo, Callback<ListBranches> callback);

    @GET("/repos/{owner}/{repo}/releases")
    void releases(@Path("owner") String owner, @Path("repo") String repo, Callback<ListReleases> callback);

    @GET("/repos/{owner}/{repo}/issues")
    void issues(@Path("owner") String owner, @Path("repo") String repo, Callback<ListIssues> callback);

    @GET("/repos/{owner}/{repo}/contents")
    void contents(@Path("owner") String owner, @Path("repo") String repo, Callback<ListContents> callback);

    @GET("/repos/{owner}/{repo}/contents")
    void contentsByRef(@Path("owner") String owner, @Path("repo") String repo, @Query("ref") String ref, Callback<ListContents> callback);

    @GET("/repos/{owner}/{repo}/readme")
    void readme(@Path("owner") String owner, @Path("repo") String repo, Callback<Content> callback);

    @GET("/repos/{owner}/{repo}/readme")
    void readme(@Path("owner") String owner, @Path("repo") String repo, @Query("ref") String ref, Callback<Content> callback);

    @GET("/repos/{owner}/{repo}/contents/{path}")
    void contents(@Path("owner") String owner, @Path("repo") String repo, @Path("path") String path, Callback<ListContents> callback);

    @GET("/repos/{owner}/{repo}/contents/{path}")
    void contentsByRef(@Path("owner") String owner, @Path("repo") String repo, @Path("path") String path, @Query("ref") String ref,  Callback<ListContents> callback);

    @GET("/repos/{owner}/{repo}/stats/contributors")
    void contributors(@Path("owner") String owner, @Path("repo") String repo, Callback<ListContributors> callback);
}

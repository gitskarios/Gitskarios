package com.alorma.github.sdk.services.repo;

import com.alorma.github.sdk.bean.dto.response.Repo;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Part;
import retrofit.http.Path;

/**
 * Created by Bernat on 17/07/2014.
 */
public interface RepoService {

    @GET("/repos/{owner}/{repo}")
    void get(@Path("owner") String owner, @Path("repo") String repo, Callback<Repo> callback);
}

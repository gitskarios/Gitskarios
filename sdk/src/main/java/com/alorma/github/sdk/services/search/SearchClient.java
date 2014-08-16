package com.alorma.github.sdk.services.search;

import com.alorma.github.sdk.bean.dto.response.search.IssuesSearch;
import com.alorma.github.sdk.bean.dto.response.search.ReposSearch;
import com.alorma.github.sdk.bean.dto.response.search.UsersSearch;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Bernat on 08/08/2014.
 */
public interface SearchClient {

    @GET("/search/repositories")
    void repos(@Query("q") String query, Callback<ReposSearch> callback);

    @GET("/search/repositories")
    void reposPaginated(@Query("q") String query, @Query("page") int page, Callback<ReposSearch> callback);

    @GET("/search/issues")
    void issues(@Query("q") String query, Callback<IssuesSearch> callback);

    @GET("/search/users")
    void users(@Query("q") String query, Callback<UsersSearch> callback);

}

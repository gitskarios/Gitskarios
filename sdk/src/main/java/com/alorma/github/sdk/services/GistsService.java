package com.alorma.github.sdk.services;

import com.alorma.github.sdk.bean.dto.response.ListGists;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by Bernat on 08/07/2014.
 */
public interface GistsService {

    @GET("/gists")
    void userGistsList(Callback<ListGists> callback);

}

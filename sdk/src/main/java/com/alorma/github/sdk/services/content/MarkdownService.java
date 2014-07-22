package com.alorma.github.sdk.services.content;

import com.alorma.github.sdk.bean.dto.request.RequestReadmeDTO;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by Bernat on 22/07/2014.
 */
public interface MarkdownService {

    @POST("/markdown/raw")
    void markdown(@Body RequestReadmeDTO readme, Callback<String> str);
}

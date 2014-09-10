package com.alorma.github.sdk.services.content;

import com.alorma.github.sdk.bean.dto.request.RequestMarkdownDTO;
import com.alorma.github.sdk.bean.dto.response.Content;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Bernat on 22/07/2014.
 */
public interface ContentService {

	@POST("/markdown/raw")
	void markdown(@Body RequestMarkdownDTO readme, Callback<String> callback);

	@GET("/repos/{owner}/{repo}/contents/{path}")
	void fileContent(@Path("owner") String owner, @Path("repo") String repo, @Path("path") String path, Callback<Content> callback);

	@GET("/repos/{owner}/{repo}/contents/{path}")
	void fileContent(@Path("owner") String owner, @Path("repo") String repo, @Path("path") String path, @Query("sha") String sha, Callback<Content> callback);
}

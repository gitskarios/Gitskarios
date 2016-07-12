package com.alorma.github.sdk.services.content;

import com.alorma.github.sdk.bean.dto.request.NewContentRequest;
import com.alorma.github.sdk.bean.dto.response.Content;
import com.alorma.github.sdk.bean.dto.response.NewContentResponse;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface ContentService {

  //Sync
  @POST("/markdown/raw")
  Observable<String> markdown(@Body String readme);

  @GET("/repos/{owner}/{name}/contents/{path}")
  Observable<Content> fileContent(@Path("owner") String owner, @Path("name") String repo,
      @Path(value = "path", encode = false) String path);

  @GET("/repos/{owner}/{name}/contents/{path}")
  Observable<Content> fileContentSha(@Path("owner") String owner, @Path("name") String repo,
      @Path(value = "path", encode = false) String path, @Query("sha") String sha);

  @GET("/repos/{owner}/{name}/contents/{path}")
  Observable<Content> fileContentRef(@Path("owner") String owner, @Path("name") String repo,
      @Path(value = "path", encode = false) String path, @Query("ref") String ref);

  @GET("/repos/{owner}/{name}/{file_type}/{path}")
  Observable<Object> archiveLink(@Path("owner") String owner, @Path("name") String repo, @Path("file_type") String file_type,
      @Path(value = "path", encode = false) String path);

  @PUT("/repos/{owner}/{repo}/contents/{path}")
  Observable<NewContentResponse> createFile(@Path("owner") String owner, @Path("repo") String repo, @Path("path") String path,
      @Body NewContentRequest body);

  @DELETE("/repos/{owner}/{repo}/contents/{path}")
  Observable<NewContentResponse> deleteFile(@Path("owner") String owner, @Path("repo") String repo, @Path("path") String path,
      @Body NewContentRequest body);
}

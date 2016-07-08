package com.alorma.github.sdk.services.git;

import com.alorma.github.sdk.bean.dto.response.GitBlob;
import com.alorma.github.sdk.bean.dto.response.GitCommit;
import com.alorma.github.sdk.bean.dto.response.GitReference;
import com.alorma.github.sdk.bean.dto.response.GitTree;
import java.util.List;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface GitDataService {

  //Async
  //Reference
  @GET("/repos/{owner}/{repo}/git/refs")
  void repoReferences(@Path("owner") String owner, @Path("repo") String name,
      Callback<List<GitReference>> callback);

  @GET("/repos/{owner}/{repo}/git/refs")
  void repoReferences(@Path("owner") String owner, @Path("repo") String name,
      @Query("page") int page, Callback<List<GitReference>> callback);

  //Sync

  @GET("/repos/{owner}/{repo}/git/{ref}")
  Observable<GitReference> repoReference(@Path("owner") String owner, @Path("repo") String name,
      @Path(value = "ref", encode = false) String ref);

  @GET("/repos/{owner}/{repo}/git/commits/{sha}")
  Observable<GitCommit> repoCommit(@Path("owner") String owner, @Path("repo") String name,
      @Path("sha") String sha);

  @GET("/repos/{owner}/{repo}/git/trees/{sha}")
  Observable<GitTree> repoTree(@Path("owner") String owner, @Path("repo") String name,
      @Path("sha") String sha);

  @GET("/repos/{owner}/{repo}/git/trees/{sha}?recursive=1")
  Observable<GitTree> repoTreeRecursive(@Path("owner") String owner, @Path("repo") String name,
      @Path("sha") String sha);

  @GET("/repos/{owner}/{repo}/git/blobs/{sha}")
  Observable<GitBlob> repoBlob(@Path("owner") String owner, @Path("repo") String name,
      @Path("sha") String sha);
}

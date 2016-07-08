package com.alorma.github.sdk.services.pullrequest;

import com.alorma.github.sdk.bean.dto.request.MergeButtonRequest;
import com.alorma.github.sdk.bean.dto.response.Commit;
import com.alorma.github.sdk.bean.dto.response.CommitFile;
import com.alorma.github.sdk.bean.dto.response.MergeButtonResponse;
import com.alorma.github.sdk.bean.dto.response.PullRequest;
import com.alorma.github.sdk.bean.dto.response.ReviewComment;
import java.util.List;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by Bernat on 22/08/2014.
 */
public interface PullRequestsService {

  //Async
  @GET("/repos/{owner}/{name}/pulls")
  void pulls(@Path("owner") String owner, @Path("name") String repo, @Query("state") String state,
      Callback<List<PullRequest>> callback);

  @GET("/repos/{owner}/{name}/pulls")
  void pulls(@Path("owner") String owner, @Path("name") String repo, @Query("state") String state,
      @Query("page") int page, Callback<List<PullRequest>> callback);

  @GET("/repos/{owner}/{name}/pulls/{number}")
  void pull(@Path("owner") String owner, @Path("name") String repo, @Path("number") int number,
      Callback<PullRequest> callback);

  @GET("/repos/{owner}/{repo}/pulls/{number}/commits")
  void commits(@Path("owner") String owner, @Path("repo") String repo, @Path("number") int number,
      Callback<List<Commit>> callback);

  @GET("/repos/{owner}/{repo}/pulls/{number}/commits")
  void commits(@Path("owner") String owner, @Path("repo") String repo, @Path("number") int number,
      @Query("page") int page, Callback<List<Commit>> callback);

  @GET("/repos/{owner}/{repo}/pulls/{number}/files")
  void files(@Path("owner") String owner, @Path("repo") String repo, @Path("number") int number,
      Callback<List<CommitFile>> callback);

  @GET("/repos/{owner}/{repo}/pulls/{number}/files")
  void files(@Path("owner") String owner, @Path("repo") String repo, @Path("number") int number,
      @Query("page") int page, Callback<List<CommitFile>> callback);

  @PUT("/repos/{owner}/{repo}/pulls/{number}/merge")
  void merge(@Path("owner") String owner, @Path("repo") String repo, @Path("number") int number,
      @Body MergeButtonRequest mergeButtonRequest, Callback<MergeButtonResponse> callback);

  @GET("/repos/{owner}/{repo}/pulls/{number}/comments")
  void reviewComments(@Path("owner") String owner, @Path("repo") String repo,
      @Path("number") int number, Callback<List<ReviewComment>> callback);

  @GET("/repos/{owner}/{repo}/pulls/{number}/comments")
  void reviewComments(@Path("owner") String owner, @Path("repo") String repo,
      @Path("number") int number, @Query("page") int page, Callback<List<ReviewComment>> callback);

  //Sync
  @GET("/repos/{owner}/{name}/pulls")
  List<PullRequest> pulls(@Path("owner") String owner, @Path("name") String repo,
      @Query("state") String state);

  @GET("/repos/{owner}/{name}/pulls")
  List<PullRequest> pulls(@Path("owner") String owner, @Path("name") String repo,
      @Query("state") String state, @Query("page") int page);

  @GET("/repos/{owner}/{name}/pulls/{number}")
  Observable<PullRequest> pull(@Path("owner") String owner, @Path("name") String repo,
      @Path("number") int number);

  @GET("/repos/{owner}/{repo}/pulls/{number}/commits")
  List<Commit> commits(@Path("owner") String owner, @Path("repo") String repo,
      @Path("number") int number);

  @GET("/repos/{owner}/{repo}/pulls/{number}/commits")
  List<Commit> commits(@Path("owner") String owner, @Path("repo") String repo,
      @Path("number") int number, @Query("page") int page);

  @GET("/repos/{owner}/{repo}/pulls/{number}/files")
  List<CommitFile> files(@Path("owner") String owner, @Path("repo") String repo,
      @Path("number") int number);

  @GET("/repos/{owner}/{repo}/pulls/{number}/files")
  List<CommitFile> files(@Path("owner") String owner, @Path("repo") String repo,
      @Path("number") int number, @Query("page") int page);

  @PUT("/repos/{owner}/{repo}/pulls/{number}/merge")
  Observable<MergeButtonResponse> merge(@Path("owner") String owner, @Path("repo") String repo,
      @Path("number") int number, @Body MergeButtonRequest mergeButtonRequest);

  @GET("/repos/{owner}/{repo}/pulls/{number}/comments")
  List<ReviewComment> reviewComments(@Path("owner") String owner, @Path("repo") String repo,
      @Path("number") int number);

  @GET("/repos/{owner}/{repo}/pulls/{number}/comments")
  List<ReviewComment> reviewComments(@Path("owner") String owner, @Path("repo") String repo,
      @Path("number") int number, @Query("page") int page);
}

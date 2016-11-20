package com.alorma.github.sdk.services.issues;

import com.alorma.github.sdk.bean.dto.request.CreateMilestoneRequestDTO;
import com.alorma.github.sdk.bean.dto.request.EditIssueRequestDTO;
import com.alorma.github.sdk.bean.dto.request.IssueRequest;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.Milestone;
import core.GithubComment;
import core.issues.Label;
import java.util.List;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface IssuesService {

  @GET("/repos/{owner}/{name}/milestones")
    //State can be open, closed and all
  void milestones(@Path("owner") String owner, @Path("name") String repo,
      @Query("state") String state, Callback<List<Milestone>> callback);

  @GET("/repos/{owner}/{name}/milestones")
    //State can be open, closed and all
  void milestones(@Path("owner") String owner, @Path("name") String repo,
      @Query("state") String state, @Query("page") int page, Callback<List<Milestone>> callback);

  @GET("/repos/{owner}/{name}/labels")
  void labels(@Path("owner") String owner, @Path("name") String repo,
      Callback<List<Label>> callback);

  @GET("/repos/{owner}/{name}/labels")
  void labels(@Path("owner") String owner, @Path("name") String repo, @Query("page") int page,
      Callback<List<Label>> callback);

  //Sync
  @POST("/repos/{owner}/{name}/issues")
  Observable<Issue> create(@Path("owner") String owner, @Path("name") String repo,
      @Body IssueRequest issue);

  @PATCH("/repos/{owner}/{name}/issues/{num}")
  Observable<Issue> closeIssue(@Path("owner") String owner, @Path("name") String repo,
      @Path("num") int num, @Body IssueRequest issueRequest);

  @POST("/repos/{owner}/{name}/issues/{num}/comments")
  Observable<GithubComment> addComment(@Path("owner") String owner, @Path("name") String repo,
      @Path("num") int num, @Body GithubComment comment);

  @POST("/repos/{owner}/{name}/milestones")
  Observable<Milestone> createMilestone(@Path("owner") String owner, @Path("name") String repo,
      @Body CreateMilestoneRequestDTO createMilestoneRequestDTO);

  @PATCH("/repos/{owner}/{name}/issues/{number}")
  Observable<Issue> editIssue(@Path("owner") String owner, @Path("name") String repo,
      @Path("number") int number, @Body EditIssueRequestDTO editIssueRequestDTO);
}

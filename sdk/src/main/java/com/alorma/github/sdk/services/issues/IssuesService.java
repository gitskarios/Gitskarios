package com.alorma.github.sdk.services.issues;

import com.alorma.github.sdk.bean.dto.request.IssueRequest;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.IssueComment;
import com.alorma.github.sdk.bean.dto.response.ListEvents;
import com.alorma.github.sdk.bean.dto.response.ListIssueComments;
import com.alorma.github.sdk.bean.dto.response.ListIssues;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Bernat on 22/08/2014.
 */
public interface IssuesService {

    @GET("/repos/{owner}/{repo}/issues?state=all&sort=updated")
    void issues(@Path("owner") String owner, @Path("repo") String repo, Callback<ListIssues> callback);

    @GET("/repos/{owner}/{repo}/issues?state=all&sort=updated")
    void issues(@Path("owner") String owner, @Path("repo") String repo, @Query("page") int page, Callback<ListIssues> callback);

    @POST("/repos/{owner}/{repo}/issues")
    void create(@Path("owner") String owner, @Path("repo") String repo, @Body IssueRequest issue, Callback<Issue> callback);

    @GET("/repos/{owner}/{repo}/issues/{num}")
    void detail(@Path("owner") String owner, @Path("repo") String repo, @Path("num") int num, Callback<Issue> callback);

    @GET("/repos/{owner}/{repo}/issues/{num}/comments")
    void comments(@Path("owner") String owner, @Path("repo") String repo, @Path("num") int num, Callback<ListIssueComments> callback);

    @GET("/repos/{owner}/{repo}/issues/{num}/comments")
    void comments(@Path("owner") String owner, @Path("repo") String repo, @Path("num") int num, @Query("page") int page, Callback<ListIssueComments> callback);

	@GET("/repos/{owner}/{repo}/issues/{num}/events")
	void events(@Path("owner") String owner, @Path("repo") String repo, @Path("num") int num,Callback<ListEvents> callback);

	@GET("/repos/{owner}/{repo}/issues/{num}/events")
	void events(@Path("owner") String owner, @Path("repo") String repo, @Path("num") int num, @Query("page") int page, Callback<ListEvents> callback);

	@PATCH("/repos/{owner}/{repo}/issues/{num}")
	void closeIssue(@Path("owner") String owner, @Path("repo") String repo, @Path("num") int num, @Body IssueRequest issueRequest, Callback<Issue> callback);

	@POST("/repos/{owner}/{repo}/issues/{num}/comments")
	void addComment(@Path("owner") String owner, @Path("repo") String repo, @Path("num") int num, @Body IssueComment comment, Callback<IssueComment> callback);
}

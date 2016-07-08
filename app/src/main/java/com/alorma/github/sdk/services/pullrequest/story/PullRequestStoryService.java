package com.alorma.github.sdk.services.pullrequest.story;

import com.alorma.github.sdk.bean.dto.response.PullRequest;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by Bernat on 22/08/2014.
 */
public interface PullRequestStoryService {

  //Async
  @GET("/repos/{owner}/{name}/pulls/{num}")
  void detail(@Path("owner") String owner, @Path("name") String repo, @Path("num") int num,
      Callback<PullRequest> issueCallback);

  //Sync
  @GET("/repos/{owner}/{name}/pulls/{num}")
  PullRequest detail(@Path("owner") String owner, @Path("name") String repo, @Path("num") int num);

  //Obs
  @Headers({"Accept: application/vnd.github.squirrel-girl-preview"})
  @GET("/repos/{owner}/{name}/pulls/{num}")
  Observable<PullRequest> detailObs(@Path("owner") String owner, @Path("name") String repo,
      @Path("num") int num);
}

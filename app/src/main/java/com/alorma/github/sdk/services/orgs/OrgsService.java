package com.alorma.github.sdk.services.orgs;

import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import core.User;
import java.util.List;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface OrgsService {

  //Async
  @GET("/user/orgs")
  void orgs(Callback<List<User>> callback);

  @GET("/users/{orgName}/orgs")
  void orgsByUser(@Path("orgName") String username, Callback<List<User>> callback);

  @GET("/user/orgs")
  void orgs(@Query("page") int page, Callback<List<User>> callback);

  @GET("/users/{orgName}/orgs")
  void orgsByUser(@Path("orgName") String username, @Query("page") int page, Callback<List<User>> callback);

  @GET("/orgs/{org}/events")
  void events(@Path("org") String org, Callback<List<GithubEvent>> callback);

  @GET("/orgs/{org}/events")
  void events(@Path("org") String org, @Query("page") int page, Callback<List<GithubEvent>> callback);

  //Sync
  @GET("/user/orgs")
  List<User> orgs();

  @GET("/users/{orgName}/orgs")
  List<User> orgsByUser(@Path("orgName") String username);

  @GET("/user/orgs")
  List<User> orgs(@Query("page") int page);

  @GET("/users/{orgName}/orgs")
  List<User> orgsByUser(@Path("orgName") String username, @Query("page") int page);

  @GET("/users/{orgName}/events/orgs/{org}")
  List<GithubEvent> events(@Path("orgName") String username, @Path("org") String org);

  @GET("/users/{orgName}/events/orgs/{org}")
  List<GithubEvent> events(@Path("orgName") String username, @Path("org") String org, @Query("page") int page);
}

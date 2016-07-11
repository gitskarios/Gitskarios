package com.alorma.github.sdk.services.orgs;

import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.sdk.bean.dto.response.Organization;
import java.util.List;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Bernat on 04/09/2014.
 */
public interface OrgsService {

  //Async
  @GET("/user/orgs")
  void orgs(Callback<List<Organization>> callback);

  @GET("/users/{username}/orgs")
  void orgsByUser(@Path("username") String username, Callback<List<Organization>> callback);

  @GET("/user/orgs")
  void orgs(@Query("page") int page, Callback<List<Organization>> callback);

  @GET("/users/{username}/orgs")
  void orgsByUser(@Path("username") String username, @Query("page") int page,
      Callback<List<Organization>> callback);

  @GET("/users/{username}/events/orgs/{org}")
  void events(@Path("username") String username, @Path("org") String org,
      Callback<List<GithubEvent>> callback);

  @GET("/users/{username}/events/orgs/{org}")
  void events(@Path("username") String username, @Path("org") String org, @Query("page") int page,
      Callback<List<GithubEvent>> callback);

  //Sync
  @GET("/user/orgs")
  List<Organization> orgs();

  @GET("/users/{username}/orgs")
  List<Organization> orgsByUser(@Path("username") String username);

  @GET("/user/orgs")
  List<Organization> orgs(@Query("page") int page);

  @GET("/users/{username}/orgs")
  List<Organization> orgsByUser(@Path("username") String username, @Query("page") int page);

  @GET("/users/{username}/events/orgs/{org}")
  List<GithubEvent> events(@Path("username") String username, @Path("org") String org);

  @GET("/users/{username}/events/orgs/{org}")
  List<GithubEvent> events(@Path("username") String username, @Path("org") String org,
      @Query("page") int page);
}

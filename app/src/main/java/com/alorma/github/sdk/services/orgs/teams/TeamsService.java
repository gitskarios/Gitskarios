package com.alorma.github.sdk.services.orgs.teams;

import com.alorma.github.sdk.bean.dto.response.Team;
import com.alorma.github.sdk.bean.dto.response.User;
import java.util.List;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Bernat on 04/09/2014.
 */
public interface TeamsService {

  //Async
  @GET("/orgs/{org}/teams")
  void teams(@Path("org") String org, Callback<List<Team>> callback);

  @GET("/orgs/{org}/teams")
  void teams(@Path("org") String org, @Query("page") int page, Callback<List<Team>> callback);

  @GET("/teams/{id}/members")
  void members(@Path("id") String id, Callback<List<User>> callback);

  @GET("/teams/{id}/members")
  void members(@Path("id") String id, @Query("page") int page, Callback<List<User>> callback);

  //Sync
  @GET("/orgs/{org}/teams")
  List<Team> teams(@Path("org") String org);

  @GET("/orgs/{org}/teams")
  List<Team> teams(@Path("org") String org, @Query("page") int page);

  @GET("/teams/{id}/members")
  List<User> members(@Path("id") String id);

  @GET("/teams/{id}/members")
  List<User> members(@Path("id") String id, @Query("page") int page);
}

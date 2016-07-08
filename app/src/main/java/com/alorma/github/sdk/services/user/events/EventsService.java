package com.alorma.github.sdk.services.user.events;

import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import java.util.List;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Bernat on 03/10/2014.
 */
public interface EventsService {

  //Async
  @GET("/users/{username}/received_events")
  void events(@Path("username") String username, Callback<List<GithubEvent>> eventsCallback);

  @GET("/users/{username}/received_events")
  void events(@Path("username") String username, @Query("page") int page,
      Callback<List<GithubEvent>> eventsCallback);

  @GET("/users/{username}/events")
  void createdEvents(@Path("username") String username, Callback<List<GithubEvent>> eventsCallback);

  @GET("/users/{username}/events")
  void createdEvents(@Path("username") String username, @Query("page") int page,
      Callback<List<GithubEvent>> eventsCallback);

  //Sync
  @GET("/users/{username}/received_events")
  List<GithubEvent> events(@Path("username") String username);

  @GET("/users/{username}/received_events")
  List<GithubEvent> events(@Path("username") String username, @Query("page") int page);

  @GET("/users/{username}/events")
  List<GithubEvent> createdEvents(@Path("username") String username);

  @GET("/users/{username}/events")
  List<GithubEvent> createdEvents(@Path("username") String username, @Query("page") int page);
}

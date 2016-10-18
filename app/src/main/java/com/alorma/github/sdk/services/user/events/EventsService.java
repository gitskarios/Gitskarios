package com.alorma.github.sdk.services.user.events;

import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import java.util.List;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface EventsService {

  //Async
  @GET("/users/{orgName}/received_events")
  void events(@Path("orgName") String username, Callback<List<GithubEvent>> eventsCallback);

  @GET("/users/{orgName}/received_events")
  void events(@Path("orgName") String username, @Query("page") int page,
      Callback<List<GithubEvent>> eventsCallback);

  @GET("/users/{orgName}/events")
  void createdEvents(@Path("orgName") String username, Callback<List<GithubEvent>> eventsCallback);

  @GET("/users/{orgName}/events")
  void createdEvents(@Path("orgName") String username, @Query("page") int page,
      Callback<List<GithubEvent>> eventsCallback);

  //Sync
  @GET("/users/{orgName}/received_events")
  List<GithubEvent> events(@Path("orgName") String username);

  @GET("/users/{orgName}/received_events")
  List<GithubEvent> events(@Path("orgName") String username, @Query("page") int page);

  @GET("/users/{orgName}/events")
  List<GithubEvent> createdEvents(@Path("orgName") String username);

  @GET("/users/{orgName}/events")
  List<GithubEvent> createdEvents(@Path("orgName") String username, @Query("page") int page);
}

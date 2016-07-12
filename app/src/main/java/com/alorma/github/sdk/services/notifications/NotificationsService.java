package com.alorma.github.sdk.services.notifications;

import com.alorma.github.sdk.bean.dto.request.LastDate;
import com.alorma.github.sdk.bean.dto.response.Notification;
import java.util.List;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.PATCH;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by Bernat on 18/02/2015.
 */
public interface NotificationsService {

  //obs
  @GET("/notifications")
  Observable<List<Notification>> getNotifications(@Query("all") boolean all,
      @Query("participating") boolean participating);

  @PUT("/repos/{owner}/{name}/notifications")
  Observable<Response> markAsReadRepo(@Path("owner") String owner, @Path("name") String repo);

  @PUT("/repos/{owner}/{name}/notifications")
  Observable<Response> markAsReadRepo(@Path("owner") String owner, @Path("name") String repo,
      @Body LastDate body);

  @PATCH("/notifications/threads/{id}")
  Observable<Response> markThreadAsRead(@Path("id") String id, @Body Object empty);

  @PUT("/notifications/threads/{id}/subscription")
  Observable<Response> subscribeThread(@Path("id") String id,
      @Query("subscribed") boolean subscribed, @Query("ignored") boolean ignored);

  @DELETE("/notifications/threads/{id}/subscription")
  Observable<Response> unsubscribeThread(@Path("id") String id);
}

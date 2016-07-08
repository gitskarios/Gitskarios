package com.alorma.github.sdk.services.user;

import com.alorma.github.sdk.bean.dto.response.Email;
import com.alorma.github.sdk.bean.dto.response.User;
import java.util.List;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by Bernat on 12/07/2014.
 */
public interface UsersService {

  // Followers
  @GET("/user/followers")
  void followers(Callback<List<User>> callback);

  @GET("/users/{username}/followers")
  void followers(@Path("username") String username, Callback<List<User>> callback);

  @GET("/user/followers")
  void followers(@Query("page") int page, Callback<List<User>> callback);

  @GET("/users/{username}/followers")
  void followers(@Path("username") String username, @Query("page") int page,
      Callback<List<User>> callback);

  // Following
  @GET("/user/following")
  void following(Callback<List<User>> callback);

  @GET("/users/{username}/following")
  void following(@Path("username") String username, Callback<List<User>> callback);

  @GET("/user/following")
  void following(@Query("page") int page, Callback<List<User>> callback);

  @GET("/users/{username}/following")
  void following(@Path("username") String username, @Query("page") int page,
      Callback<List<User>> callback);

  //ORGS MEMBERS

  @GET("/orgs/{org}/members")
  void orgMembers(@Path("org") String org, Callback<List<User>> callback);

  @GET("/orgs/{org}/members")
  void orgMembers(@Path("org") String org, @Query("page") int page, Callback<List<User>> callback);

  //Sync
  @GET("/users/{user}")
  Observable<User> getSingleUser(@Path("user") String user);

  @GET("/user/emails")
  Observable<List<Email>> userEmails();

  @GET("/user")
  Observable<User> me();

  // FOLLOWING USER

  @GET("/user/following/{username}")
  Observable<Response> checkFollowing(@Path("username") String username);

  @PUT("/user/following/{username}")
  Observable<Response> followUser(@Body String empty, @Path("username") String username);

  @DELETE("/user/following/{username}")
  Observable<Response> unfollowUser(@Path("username") String username);
}

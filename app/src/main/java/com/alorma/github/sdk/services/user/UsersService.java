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

  @GET("/users/{orgName}/followers")
  void followers(@Path("orgName") String username, Callback<List<User>> callback);

  @GET("/user/followers")
  void followers(@Query("page") int page, Callback<List<User>> callback);

  @GET("/users/{orgName}/followers")
  void followers(@Path("orgName") String username, @Query("page") int page,
      Callback<List<User>> callback);

  // Following
  @GET("/user/following")
  void following(Callback<List<User>> callback);

  @GET("/users/{orgName}/following")
  void following(@Path("orgName") String username, Callback<List<User>> callback);

  @GET("/user/following")
  void following(@Query("page") int page, Callback<List<User>> callback);

  @GET("/users/{orgName}/following")
  void following(@Path("orgName") String username, @Query("page") int page,
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

  @GET("/user/following/{orgName}")
  Observable<Response> checkFollowing(@Path("orgName") String username);

  @PUT("/user/following/{orgName}")
  Observable<Response> followUser(@Body String empty, @Path("orgName") String username);

  @DELETE("/user/following/{orgName}")
  Observable<Response> unfollowUser(@Path("orgName") String username);
}

package com.alorma.github.sdk.services.user;

import com.alorma.github.sdk.bean.dto.response.ListEmails;
import com.alorma.github.sdk.bean.dto.response.User;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by Bernat on 12/07/2014.
 */
public interface UsersService {

    @GET("/users/{user}")
    void getSingleUser(@Path("user") String user, Callback<User> callback);

    @GET("/user")
    void getUser(Callback<User> callback);

    @GET("/user/emails")
    void userEmails(Callback<ListEmails> callback);

}

package com.alorma.github.sdk.services;

import com.alorma.github.sdk.bean.dto.response.ListEmails;
import com.alorma.github.sdk.bean.dto.response.User;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by Bernat on 12/07/2014.
 */
public interface UserServices {
    @GET("/user/emails")
    void userEmails(Callback<ListEmails> callback);

    @GET("/user")
    void user(Callback<User> callback);
}

package com.alorma.github.sdk.services.user;

import android.content.Context;

import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.client.BaseClient;

import retrofit.RestAdapter;

/**
 * Created by Bernat on 12/07/2014.
 */
public class RequestUserClient extends BaseClient<User>{

    private String username;

    public RequestUserClient(Context context, String username) {
        super(context);
        this.username = username;
    }

    @Override
    protected void executeService(RestAdapter restAdapter) {
        UsersService usersService = restAdapter.create(UsersService.class);
        usersService.getSingleUser(username, this);
    }
}

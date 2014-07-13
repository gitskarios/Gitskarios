package com.alorma.github.sdk.services.user;

import android.content.Context;

import com.alorma.github.sdk.services.client.BaseClient;

import retrofit.RestAdapter;

/**
 * Created by Bernat on 13/07/2014.
 */
public abstract class BaseUsersClient<K> extends BaseClient<K> {
    public BaseUsersClient(Context context) {
        super(context);
    }

    @Override
    protected void executeService(RestAdapter restAdapter) {
        UsersService usersService = restAdapter.create(UsersService.class);
        executeService(usersService);
    }

    protected abstract void executeService(UsersService usersService);
}

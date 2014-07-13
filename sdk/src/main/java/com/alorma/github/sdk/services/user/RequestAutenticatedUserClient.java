package com.alorma.github.sdk.services.user;

import android.content.Context;

import com.alorma.github.sdk.bean.dto.response.User;

/**
 * Created by Bernat on 13/07/2014.
 */
public class RequestAutenticatedUserClient extends BaseUsersClient<User> {
    public RequestAutenticatedUserClient(Context context) {
        super(context);
    }

    @Override
    protected void executeService(UsersService usersService) {
        usersService.getUser(this);
    }

}

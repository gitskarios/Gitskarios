package com.alorma.github.sdk.client;

import android.content.Context;

import com.alorma.github.sdk.bean.dto.response.ListEmails;
import com.alorma.github.sdk.services.UserServices;

import retrofit.RestAdapter;

/**
 * Created by Bernat on 12/07/2014.
 */
public class UserEmailsClient extends BaseClient<ListEmails> {
    public UserEmailsClient(Context context) {
        super(context);
    }

    @Override
    protected void executeService(RestAdapter restAdapter) {
        UserServices userServices = restAdapter.create(UserServices.class);
        userServices.userEmails(this);
    }
}

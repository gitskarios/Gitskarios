package com.alorma.github.sdk.services.repos;

import android.app.Activity;
import android.content.Context;

import com.alorma.github.sdk.bean.dto.response.ListGists;
import com.alorma.github.sdk.bean.dto.response.ListRepos;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.repos.ReposService;

import retrofit.RestAdapter;

/**
 * Created by Bernat on 08/07/2014.
 */
public class UserReposClient extends BaseClient<ListRepos> {

    private String username;
    private int page = 0;

    public UserReposClient(Context context) {
        super(context);
    }

    public UserReposClient(Context context, String username) {
        super(context);
        this.username = username;
    }

    public UserReposClient(Context context, String username, int page) {
        super(context);
        this.username = username;
        this.page = page;
    }

    @Override
    protected void executeService(RestAdapter restAdapter) {
        ReposService gistsService = restAdapter.create(ReposService.class);
        if (page == 0) {
            if (username == null) {
                gistsService.userReposList(this);
            } else {
                gistsService.userReposList(username, this);
            }
        } else {
            if (username == null) {
                gistsService.userReposList(page, this);
            } else {
                gistsService.userReposList(username, page, this);
            }
        }
    }
}

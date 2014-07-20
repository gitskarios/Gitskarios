package com.alorma.github.sdk.services.repos;

import android.content.Context;

import com.alorma.github.sdk.bean.dto.response.ListRepos;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.user.UsersService;

import retrofit.RestAdapter;

/**
 * Created by Bernat on 13/07/2014.
 */
public abstract class BaseReposClient extends BaseClient<ListRepos> {
    private String username;
    private int page;

    public BaseReposClient(Context context) {
        this(context, null);
    }

    public BaseReposClient(Context context, String username) {
        this(context, username, 0);
    }
    public BaseReposClient(Context context, String username, int page) {
        super(context);
        this.username = username;
        this.page = page;
    }

    @Override
    protected void executeService(RestAdapter restAdapter) {
        ReposService reposService = restAdapter.create(ReposService.class);
        if (page == 0) {
            if (username == null) {
                executeUserFirstPage(reposService);
            } else {
                executeFirstPageByUsername(username, reposService);
            }
        } else {
            if (username == null) {
                executeUserPaginated(page, reposService);
            } else {
                executePaginatedByUsername(username, page, reposService);
            }
        }
    }

    protected abstract void executeUserFirstPage(ReposService usersService);
    protected abstract void executeFirstPageByUsername(String username, ReposService usersService);
    protected abstract void executeUserPaginated(int page, ReposService usersService);
    protected abstract void executePaginatedByUsername(String username, int page, ReposService usersService);
}

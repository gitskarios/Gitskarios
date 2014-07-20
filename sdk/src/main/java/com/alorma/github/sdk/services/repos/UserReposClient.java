package com.alorma.github.sdk.services.repos;

import android.content.Context;

public class UserReposClient extends BaseReposClient {

    public UserReposClient(Context context) {
        super(context);
    }

    public UserReposClient(Context context, String username) {
        super(context, username);
    }

    public UserReposClient(Context context, String username, int page) {
        super(context, username, page);
    }

    @Override
    protected void executeUserFirstPage(ReposService usersService) {
        usersService.userReposList(this);
    }

    @Override
    protected void executeFirstPageByUsername(String username, ReposService usersService) {
        usersService.userReposList(username, this);
    }

    @Override
    protected void executeUserPaginated(int page, ReposService usersService) {
        usersService.userReposList(page, this);
    }

    @Override
    protected void executePaginatedByUsername(String username, int page, ReposService usersService) {
        usersService.userReposList(username, page, this);
    }
}

package com.alorma.github.sdk.services.repo.actions;

import android.content.Context;

import com.alorma.github.sdk.services.client.BaseClient;

import retrofit.RestAdapter;

/**
 * Created by Bernat on 07/08/2014.
 */
public class CheckRepoWatchedClient extends BaseClient<Object> {
    private String repo;
    private String owner;

    public CheckRepoWatchedClient(Context context, String owner, String repo) {
        super(context);
        this.owner = owner;
        this.repo = repo;
    }

    @Override
    protected void executeService(RestAdapter restAdapter) {
        ActionsService actionsService = restAdapter.create(ActionsService.class);
        actionsService.checkIfRepoIsWatched(owner, repo, this);
    }
}

package com.alorma.github.sdk.services.repo.actions;

import android.content.Context;

import com.alorma.github.sdk.services.client.BaseClient;

import retrofit.RestAdapter;

/**
 * Created by Bernat on 07/08/2014.
 */
public class StarRepoClient extends BaseClient<Object> {

    private final String owner;
    private final String repo;

    public StarRepoClient(Context context, String owner, String repo) {
        super(context);
        this.owner = owner;
        this.repo = repo;
    }

    @Override
    protected void executeService(RestAdapter restAdapter) {
        restAdapter.create(ActionsService.class).starRepo(owner, repo, this);
    }
}

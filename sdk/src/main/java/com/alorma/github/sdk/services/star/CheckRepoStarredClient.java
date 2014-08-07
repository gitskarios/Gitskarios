package com.alorma.github.sdk.services.star;

import android.content.Context;

import com.alorma.github.sdk.services.client.BaseClient;

import retrofit.RestAdapter;

/**
 * Created by Bernat on 07/08/2014.
 */
public class CheckRepoStarredClient extends BaseClient<Object> {
    private String repo;
    private String owner;

    public CheckRepoStarredClient(Context context, String owner, String repo) {
        super(context);
        this.owner = owner;
        this.repo = repo;
    }

    @Override
    protected void executeService(RestAdapter restAdapter) {
        StarService starService = restAdapter.create(StarService.class);
        starService.checkIfRepoIsStarred(owner, repo, this);
    }
}

package com.alorma.github.sdk.services.repo;

import android.content.Context;

import com.alorma.github.sdk.bean.dto.response.ListRepos;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.repos.ReposService;

import retrofit.RestAdapter;

/**
 * Created by Bernat on 13/07/2014.
 */
public abstract class BaseRepoClient<K> extends BaseClient<K> {
    public String owner;
    public String repo;
    private String username;
    private int page;

    public BaseRepoClient(Context context, String owner, String repo) {
        super(context);
        this.owner = owner;
        this.repo = repo;
    }

    @Override
    protected void executeService(RestAdapter restAdapter) {
        RepoService repoService = restAdapter.create(RepoService.class);
        executeService(repoService);
    }

    protected abstract void executeService(RepoService repoService);
}

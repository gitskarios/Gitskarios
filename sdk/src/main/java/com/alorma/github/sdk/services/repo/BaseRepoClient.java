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
    private String username;
    private int page;

    public BaseRepoClient(Context context) {
        super(context);
    }

    @Override
    protected void executeService(RestAdapter restAdapter) {
        RepoService repoService = restAdapter.create(RepoService.class);
        executeService(repoService);
    }

    protected abstract void executeService(RepoService repoService);
}

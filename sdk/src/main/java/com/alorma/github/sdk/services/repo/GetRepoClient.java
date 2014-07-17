package com.alorma.github.sdk.services.repo;

import android.content.Context;

import com.alorma.github.sdk.bean.dto.response.Repo;

/**
 * Created by Bernat on 17/07/2014.
 */
public class GetRepoClient extends BaseRepoClient<Repo> {
    private String owner;
    private String repo;

    public GetRepoClient(Context context, String owner, String repo) {
        super(context);
        this.owner = owner;
        this.repo = repo;
    }

    @Override
    protected void executeService(RepoService repoService) {
        repoService.get(owner, repo, this);
    }
}

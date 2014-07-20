package com.alorma.github.sdk.services.repo;

import android.content.Context;

import com.alorma.github.sdk.bean.dto.response.Repo;

/**
 * Created by Bernat on 17/07/2014.
 */
public class GetRepoClient extends BaseRepoClient<Repo> {

    public GetRepoClient(Context context, String owner, String repo) {
        super(context, owner, repo);
    }

    @Override
    protected void executeService(RepoService repoService) {
        repoService.get(owner, repo, this);
    }
}

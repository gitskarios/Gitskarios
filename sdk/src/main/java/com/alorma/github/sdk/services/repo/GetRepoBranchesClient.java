package com.alorma.github.sdk.services.repo;

import android.content.Context;

import com.alorma.github.sdk.bean.dto.response.ListBranches;
import com.alorma.github.sdk.bean.dto.response.ListContributors;

/**
 * Created by Bernat on 20/07/2014.
 */
public class GetRepoBranchesClient extends BaseRepoClient<ListBranches> {

    public GetRepoBranchesClient(Context context, String owner, String repo) {
        super(context, owner, repo);
    }

    @Override
    protected void executeService(RepoService repoService) {
        repoService.branches(owner, repo, this);
    }
}

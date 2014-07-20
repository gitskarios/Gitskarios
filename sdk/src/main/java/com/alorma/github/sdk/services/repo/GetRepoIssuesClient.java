package com.alorma.github.sdk.services.repo;

import android.content.Context;

import com.alorma.github.sdk.bean.dto.response.ListBranches;
import com.alorma.github.sdk.bean.dto.response.ListIssues;

/**
 * Created by Bernat on 20/07/2014.
 */
public class GetRepoIssuesClient extends BaseRepoClient<ListIssues> {

    public GetRepoIssuesClient(Context context, String owner, String repo) {
        super(context, owner, repo);
    }

    @Override
    protected void executeService(RepoService repoService) {
        repoService.issues(owner, repo, this);
    }
}

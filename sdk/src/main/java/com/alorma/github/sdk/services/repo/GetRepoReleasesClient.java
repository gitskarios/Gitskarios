package com.alorma.github.sdk.services.repo;

import android.content.Context;

import com.alorma.github.sdk.bean.dto.response.ListBranches;
import com.alorma.github.sdk.bean.dto.response.ListReleases;

/**
 * Created by Bernat on 20/07/2014.
 */
public class GetRepoReleasesClient extends BaseRepoClient<ListReleases> {

    public GetRepoReleasesClient(Context context, String owner, String repo) {
        super(context, owner, repo);
    }

    @Override
    protected void executeService(RepoService repoService) {
        repoService.releases(owner, repo, this);
    }
}

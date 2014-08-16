package com.alorma.github.sdk.services.repo;

import android.content.Context;

import com.alorma.github.sdk.bean.dto.response.Branch;
import com.alorma.github.sdk.bean.dto.response.ListContents;
import com.alorma.github.sdk.bean.dto.response.ListIssues;

/**
 * Created by Bernat on 20/07/2014.
 */
public class GetRepoContentsClient extends BaseRepoClient<ListContents> {

    private String path = null;
    private Branch currentBranch;

    public GetRepoContentsClient(Context context, String owner, String repo) {
        super(context, owner, repo);
    }

    public GetRepoContentsClient(Context context, String owner, String repo, String path) {
        super(context, owner, repo);
        this.path = path;
    }

    @Override
    protected void executeService(RepoService repoService) {
        if (path == null) {
            if (currentBranch == null) {
                repoService.contents(owner, repo, this);
            } else {
                repoService.contentsByRef(owner, repo, currentBranch.name, this);
            }
        } else {
            if (currentBranch == null) {
                repoService.contents(owner, repo, path, this);
            } else {
                repoService.contentsByRef(owner, repo, path, currentBranch.name, this);
            }
        }
    }

    public void setCurrentBranch(Branch currentBranch) {
        this.currentBranch = currentBranch;
    }
}

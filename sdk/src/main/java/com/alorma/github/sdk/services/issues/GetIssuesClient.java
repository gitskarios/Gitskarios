package com.alorma.github.sdk.services.issues;

import android.content.Context;

import com.alorma.github.sdk.bean.dto.response.ListIssues;

/**
 * Created by Bernat on 22/08/2014.
 */
public class GetIssuesClient extends BaseIssuesClient<ListIssues> {
    private int page;

    public GetIssuesClient(Context context, String owner, String repository) {
        super(context, owner, repository);
        this.page = 0;
    }

    public GetIssuesClient(Context context, String owner, String repository, int page) {
        super(context, owner, repository, page);
    }

    @Override
    protected void executeFirstPage(String owner, String repository, IssuesService issuesService) {
        issuesService.issues(owner, repository, this);
    }

    @Override
    protected void executePaginated(String owner, String repository, int page, IssuesService issuesService) {
        issuesService.issues(owner, repository, page, this);
    }
}

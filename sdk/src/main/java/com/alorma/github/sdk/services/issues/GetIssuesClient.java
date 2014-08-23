package com.alorma.github.sdk.services.issues;

import android.content.Context;

/**
 * Created by Bernat on 22/08/2014.
 */
public class GetIssuesClient extends BaseIssuesClient {
    private int page;

    public GetIssuesClient(Context context, String owner, String repository) {
        super(context, owner, repository);
        this.page = 0;
    }

    public GetIssuesClient(Context context, String owner, String repository, int page) {
        super(context, owner, repository, page);
    }

    @Override
    protected void executeRepoFirstPage(String owner, String repository, IssuesService issuesService) {
        issuesService.issues(owner, repository, this);
    }

    @Override
    protected void executeRepoPaginated(String owner, String repository, int page, IssuesService issuesService) {
        issuesService.issues(owner, repository, this);
    }
}

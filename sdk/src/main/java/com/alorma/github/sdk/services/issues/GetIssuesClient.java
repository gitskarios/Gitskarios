package com.alorma.github.sdk.services.issues;

import android.content.Context;

/**
 * Created by Bernat on 22/08/2014.
 */
public class GetIssuesClient extends BaseIssuesClient {
    public GetIssuesClient(Context context, String owner, String repository) {
        super(context, owner, repository);
    }

    @Override
    protected void executeRepoFirstPage(IssuesService issuesService, String owner, String repository) {
        issuesService.issues(owner, repository, this);
    }

    @Override
    protected void executeFirstPageByRepo(String username, IssuesService issuesService) {

    }

    @Override
    protected void executeRepoPaginated(int page, IssuesService issuesService) {

    }

    @Override
    protected void executePaginatedByRepo(String username, int page, IssuesService issuesService) {

    }
}

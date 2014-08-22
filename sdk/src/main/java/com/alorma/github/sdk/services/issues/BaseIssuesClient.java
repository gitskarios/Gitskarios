package com.alorma.github.sdk.services.issues;

import android.content.Context;

import com.alorma.github.sdk.bean.dto.response.ListIssues;
import com.alorma.github.sdk.services.client.BaseClient;

import retrofit.RestAdapter;

/**
 * Created by Bernat on 22/08/2014.
 */
public abstract class BaseIssuesClient extends BaseClient<ListIssues> {
    private String owner;
    private String repository;

    public BaseIssuesClient(Context context, String owner, String repository) {
        super(context);
        this.owner = owner;
        this.repository = repository;
    }

    @Override
    protected void executeService(RestAdapter restAdapter) {
        IssuesService issuesService = restAdapter.create(IssuesService.class);
        executeRepoFirstPage(issuesService, owner, repository);
    }

    protected abstract void executeRepoFirstPage(IssuesService issuesService, String owner, String repository);

    protected abstract void executeFirstPageByRepo(String username, IssuesService issuesService);

    protected abstract void executeRepoPaginated(int page, IssuesService issuesService);

    protected abstract void executePaginatedByRepo(String username, int page, IssuesService issuesService);

    @Override
    public String getAcceptHeader() {
        return "application/vnd.github.v3.text+json";
    }
}

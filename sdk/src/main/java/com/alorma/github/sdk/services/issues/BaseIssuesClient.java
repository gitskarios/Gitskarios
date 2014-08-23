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
    private int page;

    public BaseIssuesClient(Context context, String owner, String repository) {
        super(context);
        this.owner = owner;
        this.repository = repository;
    }

    public BaseIssuesClient(Context context, String owner, String repository, int page) {
        super(context);
        this.owner = owner;
        this.repository = repository;
        this.page = page;
    }

    @Override
    protected void executeService(RestAdapter restAdapter) {
        IssuesService issuesService = restAdapter.create(IssuesService.class);
        if (page == 0) {
            executeRepoFirstPage(owner, repository, issuesService);
        } else {
            executeRepoPaginated(owner, repository, page, issuesService);
        }
    }

    protected abstract void executeRepoFirstPage(String owner, String repository, IssuesService issuesService);

    protected abstract void executeRepoPaginated(String owner, String repository, int page, IssuesService issuesService);

    @Override
    public String getAcceptHeader() {
        return "application/vnd.github.v3.text+json";
    }
}

package com.alorma.github.sdk.services.issues;

import android.content.Context;

import com.alorma.github.sdk.services.client.BaseClient;

import retrofit.RestAdapter;

/**
 * Created by Bernat on 22/08/2014.
 */
public abstract class BaseIssuesClient<K> extends BaseClient<K> {
    private String owner;
    private String repository;
	private int num;
	private int page;

    public BaseIssuesClient(Context context, String owner, String repository, int num) {
        super(context);
        this.owner = owner;
        this.repository = repository;
		this.num = num;
	}

	public BaseIssuesClient(Context context, String owner, String repository, int num, int page) {
		this(context, owner, repository, num);
		this.page = page;
	}

    @Override
    protected void executeService(RestAdapter restAdapter) {
        IssuesService issuesService = restAdapter.create(IssuesService.class);
        if (page == 0) {
            executeFirstPage(owner, repository, num, issuesService);
        } else {
            executePaginated(owner, repository, num, page, issuesService);
        }
    }

    protected abstract void executeFirstPage(String owner, String repository, int num, IssuesService issuesService);

    protected abstract void executePaginated(String owner, String repository, int num, int page, IssuesService issuesService);

    @Override
    public String getAcceptHeader() {
        return "application/vnd.github.v3.text+json";
    }
}

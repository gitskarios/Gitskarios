package com.alorma.github.sdk.services.issues;

import android.content.Context;

import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.services.client.BaseClient;

import retrofit.RestAdapter;

/**
 * Created by Bernat on 13/09/2014.
 */
public class GetIssueClient extends BaseClient<Issue> {
	private String owner;
	private String repo;
	private int number;

	public GetIssueClient(Context context, IssueInfo issueInfo) {
		super(context);
		this.owner = issueInfo.owner;
		this.repo = issueInfo.repo;
		this.number = issueInfo.num;
	}

	@Override
	protected void executeService(RestAdapter restAdapter) {
		restAdapter.create(IssuesService.class).detail(owner, repo, number, this);
	}
}

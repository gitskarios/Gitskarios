package com.alorma.github.sdk.services.issues;

import android.content.Context;

import com.alorma.github.sdk.bean.dto.request.IssueRequest;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.IssueState;
import com.alorma.github.sdk.services.client.BaseClient;

import retrofit.RestAdapter;

/**
 * Created by Bernat on 01/09/2014.
 */
public class CloseIssueClient extends BaseClient<Issue> {

	private final IssueRequest issueRequest;
	private String owner;
	private String repo;
	private int num;

	public CloseIssueClient(Context context, String owner, String repo, int num) {
		super(context);

		this.owner = owner;
		this.repo = repo;
		this.num = num;

		this.issueRequest = new IssueRequest();
		issueRequest.state = IssueState.closed;
	}

	@Override
	protected void executeService(RestAdapter restAdapter) {
		restAdapter.create(IssuesService.class).closeIssue(owner, repo, num, issueRequest, this);
	}
}

package com.alorma.github.sdk.services.issues;

import android.content.Context;

import com.alorma.github.sdk.bean.dto.response.ListIssues;

/**
 * Created by Bernat on 22/08/2014.
 */
public class GetIssuesClient extends BaseIssuesClient<ListIssues> {

	public GetIssuesClient(Context context, String owner, String repository, int num) {
		super(context, owner, repository, num);
	}

	public GetIssuesClient(Context context, String owner, String repository, int num, int page) {
		super(context, owner, repository, num, page);
	}

	@Override
	protected void executeFirstPage(String owner, String repository, int num, IssuesService issuesService) {
		issuesService.issues(owner, repository, this);
	}

	@Override
	protected void executePaginated(String owner, String repository, int num, int page, IssuesService issuesService) {
		issuesService.issues(owner, repository, page, this);
	}
}

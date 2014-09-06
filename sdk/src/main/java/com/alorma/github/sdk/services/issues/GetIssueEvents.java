package com.alorma.github.sdk.services.issues;

import android.content.Context;

import com.alorma.github.sdk.bean.dto.response.ListEvents;

/**
 * Created by Bernat on 31/08/2014.
 */
public class GetIssueEvents extends BaseIssuesClient<ListEvents> {

	public GetIssueEvents(Context context, String owner, String repository, int num) {
		super(context, owner, repository, num);
	}

	public GetIssueEvents(Context context, String owner, String repository, int num, int page) {
		super(context, owner, repository, num, page);
	}

	@Override
	protected void executeFirstPage(String owner, String repository, int num, IssuesService issuesService) {
		issuesService.events(owner, repository, num, this);
	}

	@Override
	protected void executePaginated(String owner, String repository, int num, int page, IssuesService issuesService) {
		issuesService.events(owner, repository, num, page, this);
	}
}

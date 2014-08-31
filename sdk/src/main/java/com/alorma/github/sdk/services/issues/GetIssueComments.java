package com.alorma.github.sdk.services.issues;

import android.content.Context;

import com.alorma.github.sdk.bean.dto.response.ListIssueComments;
import com.alorma.github.sdk.services.issues.BaseIssuesClient;
import com.alorma.github.sdk.services.issues.IssuesService;

/**
 * Created by Bernat on 23/08/2014.
 */
public class GetIssueComments extends BaseIssuesClient<ListIssueComments> {

	public GetIssueComments(Context context, String owner, String repository, int num) {
		super(context, owner, repository, num);
	}

	public GetIssueComments(Context context, String owner, String repository, int num, int page) {
		super(context, owner, repository, num, page);
	}

	@Override
    protected void executeFirstPage(String owner, String repository, int num, IssuesService issuesService) {
        issuesService.comments(owner, repository, num, this);
    }

    @Override
    protected void executePaginated(String owner, String repository, int num, int page, IssuesService issuesService) {
        issuesService.comments(owner, repository, num, page, this);
    }

    @Override
    public String getAcceptHeader() {
        return "application/vnd.github.v3.html+json";
    }
}

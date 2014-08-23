package com.alorma.github.sdk.services.issues.comments;

import android.content.Context;

import com.alorma.github.sdk.bean.dto.response.ListIssueComments;
import com.alorma.github.sdk.services.issues.BaseIssuesClient;
import com.alorma.github.sdk.services.issues.IssuesService;

/**
 * Created by Bernat on 23/08/2014.
 */
public class GetIssueComments extends BaseIssuesClient<ListIssueComments> {
    private int num;

    public GetIssueComments(Context context, String owner, String repository, int num) {
        super(context, owner, repository);
        this.num = num;
    }

    public GetIssueComments(Context context, String owner, String repository, int num, int page) {
        this(context, owner, repository, num);
    }

    @Override
    protected void executeFirstPage(String owner, String repository, IssuesService issuesService) {
        issuesService.comments(owner, repository, num, this);
    }

    @Override
    protected void executePaginated(String owner, String repository, int page, IssuesService issuesService) {
        issuesService.comments(owner, repository, num, page, this);
    }

    @Override
    public String getAcceptHeader() {
        return "application/vnd.github.v3.html+json";
    }
}

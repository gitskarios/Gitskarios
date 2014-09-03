package com.alorma.github.sdk.services.repos;

import android.content.Context;

public class OrgsReposClient extends BaseReposClient {

    public OrgsReposClient(Context context, String org) {
        super(context, org);
    }

    public OrgsReposClient(Context context, String org, int page) {
        super(context, org, page);
    }

    @Override
    protected void executeUserFirstPage(ReposService orgsService) {

    }

    @Override
    protected void executeFirstPageByUsername(String org, ReposService orgsService) {
		orgsService.orgsReposList(org, this);
    }

    @Override
    protected void executeUserPaginated(int page, ReposService orgsService) {

    }

    @Override
    protected void executePaginatedByUsername(String org, int page, ReposService orgsService) {
		orgsService.orgsReposList(org, page, this);
    }
}

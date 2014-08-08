package com.alorma.github.sdk.services.search;

import android.content.Context;

import com.alorma.github.sdk.bean.dto.response.search.ReposSearch;

/**
 * Created by Bernat on 08/08/2014.
 */
public class RepoSearchClient extends BaseSearchClient<ReposSearch> {

    public RepoSearchClient(Context context, String query) {
        super(context, query);
    }

    public RepoSearchClient(Context context, String query, int page) {
        super(context, query, page);
    }

    @Override
    protected void executeFirst(SearchClient searchClient, String query) {
        searchClient.repos(query, this);
    }

    @Override
    protected void executePaginated(SearchClient searchClient, String query, int page) {
        searchClient.reposPaginated(query, page, this);
    }

}

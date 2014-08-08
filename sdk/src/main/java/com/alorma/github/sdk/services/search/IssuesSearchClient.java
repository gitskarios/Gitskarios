package com.alorma.github.sdk.services.search;

import android.content.Context;

import com.alorma.github.sdk.bean.dto.response.search.IssuesSearch;
import com.alorma.github.sdk.bean.dto.response.search.ReposSearch;
import com.alorma.github.sdk.services.client.BaseClient;

import retrofit.RestAdapter;

/**
 * Created by Bernat on 08/08/2014.
 */
public class IssuesSearchClient extends BaseSearchClient<IssuesSearch> {

    public IssuesSearchClient(Context context, String query) {
        super(context, query);
    }

    public IssuesSearchClient(Context context, String query, int page) {
        super(context, query, page);
    }

    @Override
    protected void executeFirst(SearchClient searchClient, String query) {
        searchClient.issues(query, this);
    }

    @Override
    protected void executePaginated(SearchClient searchClient, String query, int page) {

    }
}

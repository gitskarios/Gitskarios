package com.alorma.github.sdk.services.search;

import android.content.Context;

import com.alorma.github.sdk.services.client.BaseClient;

import retrofit.RestAdapter;

/**
 * Created by Bernat on 08/08/2014.
 */
public abstract class BaseSearchClient<K> extends BaseClient<K> {
    protected String query;
    private int page = 0;

    public BaseSearchClient(Context context, String query) {
        super(context);
        this.query = query;
    }
    public BaseSearchClient(Context context, String query, int page) {
        super(context);
        this.query = query;
        this.page = page;
    }

    @Override
    protected void executeService(RestAdapter restAdapter) {
        executeSearch(restAdapter.create(SearchClient.class));
    }

    protected void executeSearch(SearchClient searchClient) {
        if (page == 0) {
            executeFirst(searchClient, query);
        } else {
            executePaginated(searchClient, query, page);
        }
    }

    protected abstract void executeFirst(SearchClient searchClient, String query);

    protected abstract void executePaginated(SearchClient searchClient, String query, int page);


}

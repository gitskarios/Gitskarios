package com.alorma.github.ui.fragment.base;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Toast;

import com.alorma.github.BuildConfig;
import com.alorma.github.sdk.bean.PaginationLink;
import com.alorma.github.sdk.bean.RelType;
import com.alorma.github.sdk.services.client.BaseClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;

public abstract class PaginatedListFragment<K> extends LoadingListFragment implements BaseClient.OnResultCallback<K>, AbsListView.OnScrollListener {

    protected static final String USERNAME = "USERNAME";

    private PaginationLink bottomPaginationLink;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setOnScrollListener(this);

        executeRequest();
    }

    protected abstract void executeRequest();

    @Override
    public void onScrollStateChanged(AbsListView absListView, int state) {

    }

    @Override
    public void onScroll(AbsListView absListView, int first, int last, int total) {
        if (total > 0 && first + last == total) {
            if (bottomPaginationLink != null && bottomPaginationLink.rel == RelType.next) {
                executePaginatedRequest(bottomPaginationLink.page);
                bottomPaginationLink = null;
            }
        }
    }

    protected abstract void executePaginatedRequest(int page);

    @Override
    public void onResponseOk(K k, Response r) {
        if (getActivity() != null && isAdded()) {
            if (swipe != null) {
                swipe.setRefreshing(false);
            }
            onResponse(k);
            getLinkData(r);
        }
    }

    @Override
    public void onFail(RetrofitError error) {
        if (swipe != null) {
            swipe.setRefreshing(false);
        }

        if (getActivity() != null && isAdded()) {
            onQueryFail();
        }

    }

    protected abstract void onQueryFail();

    protected abstract void onResponse(K k);

    private void getLinkData(Response r) {
        List<Header> headers = r.getHeaders();
        Map<String, String> headersMap = new HashMap<String, String>(headers.size());
        for (Header header : headers) {
            headersMap.put(header.getName(), header.getValue());
        }

        String link = headersMap.get("Link");

        if (link != null) {
            String[] parts = link.split(",");
            try {
                bottomPaginationLink = new PaginationLink(parts[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRefresh() {
        executeRequest();
        if (emptyLy != null) {
            emptyLy.setVisibility(View.GONE);
        }
    }
}

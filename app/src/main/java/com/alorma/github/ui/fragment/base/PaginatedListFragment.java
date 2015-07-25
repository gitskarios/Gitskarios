package com.alorma.github.ui.fragment.base;

import com.alorma.github.basesdk.client.BaseClient;
import com.alorma.github.sdk.bean.info.PaginationLink;
import com.alorma.github.sdk.bean.info.RelType;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;

public abstract class PaginatedListFragment<K, Adapter extends RecyclerArrayAdapter> extends LoadingListFragment<Adapter> implements BaseClient.OnResultCallback<K> {

    protected static final String USERNAME = "USERNAME";
    private PaginationLink bottomPaginationLink;

    protected boolean refreshing;

    @Override
    public void onResponseOk(K k, Response r) {
        stopRefresh();
        hideEmpty();
        if (getActivity() != null && isAdded()) {
            if (k != null && k instanceof List) {
                if (((List) k).size() > 0) {
                    getLinkData(r);

                    if (getAdapter() != null && refreshing) {
                        getAdapter().clear();
                    }

                    onResponse(k, refreshing);
                    refreshing = false;
                } else if (getAdapter() == null || getAdapter().getItemCount() == 0) {
                    setEmpty();
                }
            }
        }
    }

    @Override
    protected void executeRequest() {
        super.executeRequest();
        bottomPaginationLink = null;
    }

    @Override
    public void onFail(RetrofitError error) {
        stopRefresh();
        if (getActivity() != null) {
            ErrorHandler.onError(getActivity(), "Paginated list fragment", error);
        }
        if (error != null && error.getResponse() != null) {
            setEmpty(error.getResponse().getStatus());
        }
    }

    protected abstract void onResponse(K k, boolean refreshing);

    private void getLinkData(Response r) {
        if (r != null) {
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
    }

    @Override
    public void onRefresh() {
        refreshing = true;
        executeRequest();
    }

    public void setRefreshing() {
        this.refreshing = true;
    }

    public boolean isRefreshing() {
        return refreshing;
    }

    @Override
    public void loadMoreItems() {
        if (bottomPaginationLink != null && bottomPaginationLink.rel == RelType.next) {
            executePaginatedRequest(bottomPaginationLink.page);
            bottomPaginationLink = null;
        }
    }
}

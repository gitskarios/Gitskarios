package com.alorma.github.ui.fragment.base;

import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import java.util.List;
import retrofit.RetrofitError;

public abstract class PaginatedListFragment<ItemType, Adapter extends RecyclerArrayAdapter>
    extends LoadingListFragment<Adapter>{

    protected static final String USERNAME = "USERNAME";

    protected boolean refreshing;
    private Integer page;

    public void onResponseOk(ItemType itemType, Integer page) {
        this.page = page;
        hideEmpty();
        if (getActivity() != null && isAdded()) {
            if (itemType != null && itemType instanceof List) {
                if (((List) itemType).size() > 0) {

                    if (getAdapter() != null && refreshing) {
                        getAdapter().clear();
                    }

                    onResponse(itemType, refreshing);
                    refreshing = false;
                } else if (getAdapter() == null || getAdapter().getItemCount() == 0) {
                    setEmpty(false);
                }
            }
        }
        stopRefresh();
    }

    @Override
    protected void executeRequest() {
        super.executeRequest();
        page = null;
    }

    private void onError(Throwable e) {
        stopRefresh();
        if (getActivity() != null) {
            ErrorHandler.onError(getActivity(), "Paginated list fragment", e);
        }
        if (e != null && e instanceof RetrofitError && ((RetrofitError) e).getResponse() != null) {
            setEmpty(true, ((RetrofitError) e).getResponse().getStatus());
        }
    }

    protected abstract void onResponse(ItemType itemType, boolean refreshing);


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
        if (page != null) {
            executePaginatedRequest(page);
            page = null;
        }
    }
}

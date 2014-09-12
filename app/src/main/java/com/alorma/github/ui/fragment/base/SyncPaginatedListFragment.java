package com.alorma.github.ui.fragment.base;

import com.alorma.github.sdk.bean.info.PaginationLink;
import com.alorma.github.sdk.bean.info.RelType;
import com.alorma.github.ui.ErrorHandler;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.AbsListView;

import java.util.List;

import retrofit.RetrofitError;

public abstract class SyncPaginatedListFragment<K> extends SyncLoadingListFragment
        implements AbsListView.OnScrollListener,
        LoaderManager.LoaderCallbacks<Pair<PaginationLink, K>> {

    protected static final String USERNAME = "USERNAME";

    private PaginationLink mBottomPaginationLink;

    protected boolean mRefreshing;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        executePaginatedRequest(1);
    }

    @Override
    public void onScroll(AbsListView absListView, int first, int last, int total) {
        if (total > 0 && first + last == total) {
            if (mBottomPaginationLink != null && mBottomPaginationLink.rel == RelType.next) {
                mRefreshing = false;
                executePaginatedRequest(mBottomPaginationLink.page);
                mBottomPaginationLink = null;
            }
        }
    }

    protected void executePaginatedRequest(int page) {
        startRefresh();
    }

    @Override
    public void onLoadFinished(final Loader<Pair<PaginationLink, K>> loader,
            final Pair<PaginationLink, K> pair) {
        stopRefresh();

        if (pair != null && pair.second instanceof List) {
            if (mEmptyView != null && ((List) pair.second).size() > 0) {
                mEmptyView.setVisibility(View.GONE);
                mBottomPaginationLink = pair.first;

                onLoadFinished(pair.second);

                mRefreshing = false;
            } else {
                setEmpty();
            }
        } else {
            setEmpty();
        }
    }

    public void onFail(RetrofitError error) {
        if (getActivity() != null) {
            stopRefresh();
            if (getListAdapter() == null || getListAdapter().getCount() == 0) {
                setEmpty();
            }
            ErrorHandler.onRetrofitError(getActivity(), "Paginated list fragment", error);
        }
    }

    protected abstract void onLoadFinished(final K response);

    @Override
    public void onRefresh() {
        mRefreshing = true;
        executePaginatedRequest(1);
    }
}

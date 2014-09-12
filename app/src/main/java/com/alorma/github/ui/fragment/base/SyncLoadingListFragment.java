package com.alorma.github.ui.fragment.base;

import com.alorma.github.R;
import com.alorma.github.ui.listeners.RefreshListener;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class SyncLoadingListFragment extends BaseListFragment
        implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe);

        if (useInnerSwipeRefresh()) {
            mSwipeRefreshLayout.setColorSchemeResources(R.color.accent,
                    R.color.gray_github_light,
                    R.color.accentDark,
                    R.color.gray_github_light);
        } else {
            mSwipeRefreshLayout.setColorSchemeResources(android.R.color.transparent,
                    android.R.color.transparent,
                    android.R.color.transparent,
                    android.R.color.transparent);
        }
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    protected boolean useInnerSwipeRefresh() {
        return true;
    }

    protected void startRefresh() {
        if (useInnerSwipeRefresh() && mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    protected void stopRefresh() {
        if (useInnerSwipeRefresh() && mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
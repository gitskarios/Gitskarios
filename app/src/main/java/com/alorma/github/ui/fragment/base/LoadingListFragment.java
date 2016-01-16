package com.alorma.github.ui.fragment.base;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alorma.github.R;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.alorma.github.utils.AttributesUtils;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.ArrayList;
import java.util.List;

import tr.xip.errorview.ErrorView;

public abstract class LoadingListFragment<Adapter extends RecyclerArrayAdapter> extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, RecyclerArrayAdapter.RecyclerAdapterContentListener,
        ErrorView.RetryListener {

    protected static final String USERNAME = "USERNAME";
    protected FloatingActionButton fab;
    protected RecyclerView recyclerView;
    protected boolean fromRetry = false;
    protected boolean refreshing;
    private SwipeRefreshLayout swipe;
    private ErrorView error_view;
    private Adapter adapter;
    private View loadingView;
    private boolean fromPaginated;
    private Integer page;
    private List<RecyclerView.ItemDecoration> listDecorators;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(useFAB() ? R.layout.list_fragment_with_fab : R.layout.list_fragment, null, false);
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);

        loadingView = view.findViewById(R.id.loading_view);

        if (recyclerView != null) {
            recyclerView.setLayoutManager(getLayoutManager());
            recyclerView.setItemAnimator(getItemAnimator());
        }

        error_view = (ErrorView) view.findViewById(R.id.error_view);

        fab = (FloatingActionButton) view.findViewById(R.id.fabButton);

        checkFAB();

        swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe);

        if (swipe != null) {
            int accent = AttributesUtils.getAccentColor(getActivity());
            swipe.setColorSchemeColors(accent);
            swipe.setOnRefreshListener(this);
        }

        if (autoStart()) {
            if (getAdapter() != null) {
                getAdapter().clear();

                setAdapter(null);
            }
            executeRequest();
        }
    }

    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    protected RecyclerView.ItemAnimator getItemAnimator() {
        return new DefaultItemAnimator();
    }

    protected void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        if (recyclerView != null) {
            if (listDecorators == null) {
                listDecorators = new ArrayList<>();
            }
            listDecorators.add(itemDecoration);
            recyclerView.removeItemDecoration(itemDecoration);
            recyclerView.addItemDecoration(itemDecoration);
        }
    }

    public void removeDecorations() {
        if (recyclerView != null) {
            if (listDecorators != null) {
                for (RecyclerView.ItemDecoration listDecorator : listDecorators) {
                    recyclerView.removeItemDecoration(listDecorator);
                }
                listDecorators.clear();
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

    @Override
    public void loadMoreItems() {
        if (page != null) {
            executePaginatedRequest(page);
            page = null;
        }
    }

    protected void executeRequest() {
        startRefresh();
    }

    protected void executePaginatedRequest(int page) {
        fromPaginated = true;
        startRefresh();
    }

    protected void startRefresh() {
        hideEmpty();
        if (swipe != null && (fromRetry || fromPaginated)) {
            fromRetry = false;
            swipe.post(new Runnable() {
                @Override
                public void run() {
                    swipe.setRefreshing(true);
                }
            });
        }

        if (!fromPaginated && loadingView != null && (getAdapter() == null || getAdapter().getItemCount() == 0)) {
            loadingView.setVisibility(View.VISIBLE);
        }
    }

    protected void stopRefresh() {
        if (swipe != null) {
            fromRetry = false;
            swipe.post(new Runnable() {
                @Override
                public void run() {
                    swipe.setRefreshing(false);
                }
            });
        }

        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
    }

    protected void checkFAB() {
        if (getActivity() != null && fab != null) {
            if (useFAB()) {
                fab.setVisibility(View.VISIBLE);
                fab.setOnClickListener(this);
                IconicsDrawable iconicsDrawable = new IconicsDrawable(getActivity(), getFABGithubIcon());
                iconicsDrawable.color(Color.WHITE);
                iconicsDrawable.sizeDp(24);

                fab.setImageDrawable(iconicsDrawable);
            } else {
                fab.setVisibility(View.GONE);
            }
        }
    }

    protected abstract void loadArguments();

    protected boolean useFAB() {
        return false;
    }

    public void setEmpty() {
        stopRefresh();
        if (getActivity() != null) {
            if (error_view != null) {
                error_view.setVisibility(View.VISIBLE);
                error_view.setTitle(getNoDataText());
                error_view.showRetryButton(true);
                error_view.setOnRetryListener(this);
            }
        }

        if (recyclerView != null) {
            recyclerView.setLayoutManager(getLayoutManager());
            recyclerView.setItemAnimator(getItemAnimator());
        }
    }

    public void setEmpty(boolean withError, int statusCode) {
        if (getActivity() != null) {
            if (error_view != null) {
                error_view.setVisibility(View.VISIBLE);
                error_view.setError(statusCode);
                error_view.showRetryButton(withError);
                if (withError) {
                    error_view.setOnRetryListener(this);
                }
            }
        }
    }

    public void hideEmpty() {
        if (getActivity() != null) {
            if (error_view != null) {
                error_view.setVisibility(View.GONE);
            }
        }
    }

    protected abstract Octicons.Icon getNoDataIcon();

    protected abstract int getNoDataText();

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fabButton) {
            fabClick();
        }
    }

    protected void fabClick() {

    }

    protected Octicons.Icon getFABGithubIcon() {
        return Octicons.Icon.oct_squirrel;
    }

    public Adapter getAdapter() {
        return adapter;
    }

    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
        if (this.adapter != null) {
            try {
                adapter.setRecyclerAdapterContentListener(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (recyclerView != null) {
            recyclerView.setAdapter(adapter);
        }
    }

    protected boolean autoStart() {
        return true;
    }

    @Override
    public void onRetry() {
        hideEmpty();
        fromRetry = true;
        executeRequest();
    }
}
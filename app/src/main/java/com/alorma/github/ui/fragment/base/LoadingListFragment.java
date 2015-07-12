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
import com.alorma.github.ui.utils.DividerItemDecoration;
import com.alorma.github.utils.AttributesUtils;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;

import tr.xip.errorview.ErrorView;

/**
 * Created by Bernat on 05/08/2014.
 */
public abstract class LoadingListFragment<Adapter extends RecyclerArrayAdapter> extends Fragment implements SwipeRefreshLayout.OnRefreshListener
        , View.OnClickListener {

    private SwipeRefreshLayout swipe;
    protected FloatingActionButton fab;
    private RecyclerView recyclerView;
    private ErrorView error_view;

    private Adapter adapter;

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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);

        if (recyclerView != null) {
            recyclerView.setLayoutManager(getLayoutManager());
            recyclerView.setItemAnimator(getItemAnimator());
            if (getItemDecoration() != null) {
                recyclerView.addItemDecoration(getItemDecoration());
            }
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
            executeRequest();
        }
    }

    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    protected RecyclerView.ItemAnimator getItemAnimator() {
        return new DefaultItemAnimator();
    }

    protected RecyclerView.ItemDecoration getItemDecoration() {
        return new DividerItemDecoration(getActivity(), DividerItemDecoration.LIST_VERTICAL);
    }

    protected void executeRequest() {
        startRefresh();
    }

    protected void executePaginatedRequest(int page) {
        startRefresh();
    }

    protected void startRefresh() {
        hideEmpty();
        if (swipe != null) {
            swipe.post(new Runnable() {
                @Override
                public void run() {
                    swipe.setRefreshing(true);
                }
            });
        }
    }

    protected void stopRefresh() {
        if (swipe != null) {
            swipe.post(new Runnable() {
                @Override
                public void run() {
                    swipe.setRefreshing(false);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        stopRefresh();
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

        if (getActivity() != null) {
            if (error_view != null) {
                error_view.setTitle(getNoDataText());
                IconicsDrawable iconicsDrawable = new IconicsDrawable(getActivity(), getNoDataIcon());
                iconicsDrawable.colorRes(R.color.gray_github_medium);
                error_view.setImage(iconicsDrawable);
            }
        }
    }

    public void setEmpty(int statusCode) {
        if (getActivity() != null) {
            if (error_view != null) {
                error_view.setError(statusCode);
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
        if (recyclerView != null) {
            recyclerView.setAdapter(adapter);
        }
    }

    protected boolean autoStart() {
        return true;
    }
}
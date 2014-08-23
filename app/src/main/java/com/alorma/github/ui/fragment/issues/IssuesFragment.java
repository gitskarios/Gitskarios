package com.alorma.github.ui.fragment.issues;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.alorma.github.sdk.bean.dto.response.ListIssues;
import com.alorma.github.sdk.services.issues.GetIssuesClient;
import com.alorma.github.ui.adapter.issues.IssuesAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.alorma.github.ui.listeners.RefreshListener;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

/**
 * Created by Bernat on 22/08/2014.
 */
public class IssuesFragment extends PaginatedListFragment<ListIssues> implements View.OnClickListener {

    private String owner;
    private String repository;
    private IssuesAdapter adapter;

    public static IssuesFragment newInstance(String owner, String repo, RefreshListener listener) {
        Bundle bundle = new Bundle();
        bundle.putString("OWNER", owner);
        bundle.putString("REPO", repo);

        IssuesFragment fragment = new IssuesFragment();
        fragment.setRefreshListener(listener);
        fragment.setArguments(bundle);
        return fragment;
    }

    protected void executeRequest() {
        super.executeRequest();
        if (owner != null && repository != null) {
            GetIssuesClient issuesClient = new GetIssuesClient(getActivity(), owner, repository);
            issuesClient.setOnResultCallback(this);
            issuesClient.execute();
        }
    }

    @Override
    protected void executePaginatedRequest(int page) {
        super.executePaginatedRequest(page);

        if (owner != null && repository != null) {
            GetIssuesClient issuesClient = new GetIssuesClient(getActivity(), owner, repository, page);
            issuesClient.setOnResultCallback(this);
            issuesClient.execute();
        }
    }

    @Override
    protected void onResponse(ListIssues issues, boolean refreshing) {
        if (issues != null && issues.size() > 0) {
            if (adapter == null || refreshing) {
                adapter = new IssuesAdapter(getActivity(), issues);
                setListAdapter(adapter);
            } else {
                adapter.addAll(issues);
            }
        }
    }

    @Override
    protected Iconify.IconValue getNoDataIcon() {
        return null;
    }

    @Override
    protected int getNoDataText() {
        return 0;
    }

    @Override
    protected void loadArguments() {
        if (getArguments() != null) {
            this.owner = getArguments().getString("OWNER");
            this.repository = getArguments().getString("REPO");
        }
        executeRequest();
    }

    @Override
    protected boolean useInnerSwipeRefresh() {
        return false;
    }

    @Override
    protected boolean useFAB() {
        return true;
    }

    @Override
    protected Drawable fabDrawable() {
        IconDrawable iconDrawable = new IconDrawable(getActivity(), Iconify.IconValue.fa_send);
        iconDrawable.color(Color.WHITE);
        iconDrawable.sizeDp(16);
        return iconDrawable;
    }

    @Override
    protected View.OnClickListener fabListener() {
        return this;
    }

    @Override
    public void onClick(View v) {

    }
}

package com.alorma.github.ui.fragment.repos;

import com.alorma.github.sdk.bean.dto.response.ListRepos;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.ui.activity.RepoDetailActivity;
import com.alorma.github.ui.adapter.repos.ReposAdapter;
import com.alorma.github.ui.fragment.base.SyncPaginatedListFragment;
import com.joanzapata.android.iconify.Iconify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

public abstract class SyncBaseReposListFragment extends SyncPaginatedListFragment<ListRepos> {

    protected ReposAdapter mReposAdapter;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mReposAdapter = new ReposAdapter(getActivity(), new ArrayList<Repo>());
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getListView().setDivider(null);
        setListAdapter(mReposAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        final Repo item = mReposAdapter.getItem(position);
        final Intent launcherActivity = RepoDetailActivity.createLauncherActivity(getActivity(),
                item.owner.login, item.name, item.description);
        startActivity(launcherActivity);
    }

    @Override
    protected void onResponse(ListRepos response, boolean refreshing) {
        stopRefresh();
        mReposAdapter.addAll(response, mRefreshing);
    }

    @Override
    protected Iconify.IconValue getNoDataIcon() {
        return Iconify.IconValue.fa_code;
    }
}

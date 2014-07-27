package com.alorma.github.ui.fragment.repos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.alorma.github.sdk.bean.dto.response.ListRepos;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.ui.activity.RepoDetailActivity;
import com.alorma.github.ui.adapter.repos.ReposAdapter;
import com.alorma.github.ui.fragment.base.PaginatedFabListFragment;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;

import java.util.ArrayList;

/**
 * Created by Bernat on 17/07/2014.
 */
public abstract class BaseReposListFragment extends PaginatedFabListFragment<ListRepos> {

    protected ReposAdapter reposAdapter;

    protected int textColor = -1;

    protected void setUpList() {
        if (textColor != -1) {
            reposAdapter = new ReposAdapter(getActivity(), new ArrayList<Repo>(), textColor);
        } else {
            reposAdapter = new ReposAdapter(getActivity(), new ArrayList<Repo>());
        }
        setListAdapter(reposAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (reposAdapter != null && reposAdapter.getCount() >= position) {
            Repo item = reposAdapter.getItem(position);
            if (item != null) {
                String repo = item.name;
                String owner = item.owner.login;
                Intent launcherActivity = RepoDetailActivity.createLauncherActivity(getActivity(), owner, repo);
                startActivity(launcherActivity);
            }
        }
    }

    @Override
    protected void onFabClick() {

    }
}

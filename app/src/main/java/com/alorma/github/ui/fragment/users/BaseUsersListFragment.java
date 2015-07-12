package com.alorma.github.ui.fragment.users;

import android.content.Intent;
import android.view.View;
import android.widget.ListView;

import com.alorma.github.sdk.bean.dto.response.ListUsers;
import com.alorma.github.ui.activity.ProfileActivity;
import com.alorma.github.ui.adapter.users.UsersAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.mikepenz.octicons_typeface_library.Octicons;

import retrofit.RetrofitError;

/**
 * Created by Bernat on 13/07/2014.
 */
public abstract class BaseUsersListFragment extends PaginatedListFragment<ListUsers> {

    private UsersAdapter usersAdapter;

    @Override
    protected void onResponse(ListUsers users, boolean refreshing) {
        getListView().setDivider(null);
        if (users.size() > 0) {
            hideEmpty();
            if (getListAdapter() != null) {
                usersAdapter.addAll(users, paging);
            } else if (usersAdapter == null) {
                setUpList(users);
            } else {
                setAdapter(usersAdapter);
            }
        } else if (usersAdapter == null || usersAdapter.getCount() == 0) {
            setEmpty();
        }
    }

    @Override
    public void onFail(RetrofitError error) {
        super.onFail(error);
        if (usersAdapter == null || usersAdapter.getCount() == 0) {
            setEmpty();
        }
    }

    protected UsersAdapter setUpList(ListUsers users) {
        usersAdapter = new UsersAdapter(getActivity(), users);
        setAdapter(usersAdapter);
        return usersAdapter;
    }

    @Override
    public void setEmpty(int statusCode) {
        super.setEmpty(statusCode);
        if (usersAdapter != null) {
            usersAdapter.clear();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (usersAdapter != null && usersAdapter.getItem(position) != null) {
            Intent launcherIntent = ProfileActivity.createLauncherIntent(getActivity(), usersAdapter.getItem(position));
            startActivity(launcherIntent);
        }
    }

    @Override
    protected Octicons.Icon getNoDataIcon() {
        return Octicons.Icon.oct_octoface;
    }

}


package com.alorma.github.ui.fragment.users;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.ListUsers;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.services.user.UserFollowersClient;
import com.alorma.github.ui.activity.ProfileActivity;
import com.alorma.github.ui.adapter.users.UsersAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import java.util.ArrayList;

/**
 * Created by Bernat on 13/07/2014.
 */
public abstract class BaseUsersListFragment extends PaginatedListFragment<ListUsers> {

    private UsersAdapter usersAdapter;

    @Override
    protected void onQueryFail() {
        IconDrawable iconDrawable = new IconDrawable(getActivity(), Iconify.IconValue.fa_group);
        iconDrawable.colorRes(R.color.gray_github_medium);
        emptyIcon.setImageDrawable(iconDrawable);

        emptyText.setText(emptyText());

        emptyLy.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResponse(ListUsers users) {
        if (users != null) {
            if (users.size() > 0) {
                if (usersAdapter == null) {
                    setUpList();
                }
                usersAdapter.addAll(users, paging);
            } else {
                onQueryFail();
            }
        }
    }

    private void setUpList() {
        usersAdapter = new UsersAdapter(getActivity(), new ArrayList<User>());
        setListAdapter(usersAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (usersAdapter != null && usersAdapter.getItem(position) != null) {
            Intent launcherIntent = ProfileActivity.createLauncherIntent(getActivity(), usersAdapter.getItem(position));
            startActivity(launcherIntent);
        }
    }

    public abstract int emptyText();
}


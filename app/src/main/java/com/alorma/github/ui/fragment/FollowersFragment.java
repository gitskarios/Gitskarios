package com.alorma.github.ui.fragment;

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
public class FollowersFragment extends PaginatedListFragment<ListUsers> {
    private String username;
    private UsersAdapter usersAdapter;

    public static FollowersFragment newInstance() {
        return new FollowersFragment();
    }

    public static FollowersFragment newInstance(String username) {
        FollowersFragment followersFragment = new FollowersFragment();
        if (username != null) {
            Bundle bundle = new Bundle();
            bundle.putString(USERNAME, username);

            followersFragment.setArguments(bundle);
        }
        return followersFragment;
    }

    @Override
    protected void executeRequest() {
        UserFollowersClient client;

        if (usersAdapter != null) {
            usersAdapter.clear();
        }

        if (getArguments() != null) {
            username = getArguments().getString(USERNAME);
        }

        client = new UserFollowersClient(getActivity(), username);

        client.setOnResultCallback(this);
        client.execute();
    }

    @Override
    protected void executePaginatedRequest(int page) {
        UserFollowersClient client = new UserFollowersClient(getActivity(), username, page);
        client.setOnResultCallback(this);
        client.execute();
    }

    @Override
    protected void onQueryFail() {
        IconDrawable iconDrawable = new IconDrawable(getActivity(), Iconify.IconValue.fa_group);
        iconDrawable.colorRes(R.color.gray_github_medium);
        emptyIcon.setImageDrawable(iconDrawable);

        emptyText.setText(R.string.no_followers);

        emptyLy.setVisibility(View.VISIBLE);
        if (swipe != null) {
            swipe.setRefreshing(false);
        }
    }

    @Override
    protected void onResponse(ListUsers users) {
        if (users != null) {
            if (swipe != null) {
                swipe.setRefreshing(false);
            }
            if (users.size() > 0) {
                if (usersAdapter == null) {
                    setUpList();
                }
                usersAdapter.addAll(users);
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
}


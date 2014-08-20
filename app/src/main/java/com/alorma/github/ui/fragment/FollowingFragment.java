package com.alorma.github.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.ListUsers;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.services.user.UserFollowersClient;
import com.alorma.github.sdk.services.user.UserFollowingClient;
import com.alorma.github.ui.activity.ProfileActivity;
import com.alorma.github.ui.adapter.users.UsersAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import java.util.ArrayList;

/**
 * Created by Bernat on 13/07/2014.
 */
public class FollowingFragment extends PaginatedListFragment<ListUsers>{
    private String username;
    private UsersAdapter usersAdapter;

    public static FollowingFragment newInstance() {
        return new FollowingFragment();
    }

    public static FollowingFragment newInstance(String username) {
        FollowingFragment followingFragment = new FollowingFragment();
        if (username != null) {
            Bundle bundle = new Bundle();
            bundle.putString(USERNAME, username);

            followingFragment.setArguments(bundle);
        }
        return followingFragment;
    }

    @Override
    protected void executeRequest() {
        UserFollowingClient client;

        if (getArguments() != null) {
            username = getArguments().getString(USERNAME);
        }

        client = new UserFollowingClient(getActivity(), username);

        client.setOnResultCallback(this);
        client.execute();
    }

    @Override
    protected void executePaginatedRequest(int page) {
        UserFollowingClient client = new UserFollowingClient(getActivity(), username, page);
        client.setOnResultCallback(this);
        client.execute();
    }

    @Override
    protected void onQueryFail() {
        IconDrawable iconDrawable = new IconDrawable(getActivity(), Iconify.IconValue.fa_group);
        iconDrawable.colorRes(R.color.gray_github_medium);
        emptyIcon.setImageDrawable(iconDrawable);

        emptyText.setText(R.string.no_followings);

        emptyLy.setVisibility(View.VISIBLE);
        if (swipe != null) {
            swipe.setRefreshing(false);
        }
    }

    @Override
    protected void onResponse(ListUsers repos) {
        if (usersAdapter == null) {
            setUpList();
        }
        if (swipe != null) {
            swipe.setRefreshing(false);
        }
        usersAdapter.addAll(repos);
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

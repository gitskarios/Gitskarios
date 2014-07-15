package com.alorma.github.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.alorma.github.sdk.bean.dto.response.ListUsers;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.services.user.UserFollowersClient;
import com.alorma.github.ui.adapter.users.UsersAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void executeRequest() {
        UserFollowersClient client;

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
    protected void onResponse(ListUsers repos) {
        if (usersAdapter == null) {
            setUpList();
        }
        usersAdapter.addAll(repos);
    }

    private void setUpList() {
        usersAdapter = new UsersAdapter(getActivity(), new ArrayList<User>());
        setListAdapter(usersAdapter);
    }
}


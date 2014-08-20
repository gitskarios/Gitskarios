package com.alorma.github.ui.fragment.users;

import android.os.Bundle;

import com.alorma.github.R;
import com.alorma.github.sdk.services.user.UserFollowersClient;

/**
 * Created by Bernat on 13/07/2014.
 */
public class FollowersFragment extends BaseUsersListFragment {
    private String username;

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
    public int emptyText() {
        return R.string.no_followers;
    }
}


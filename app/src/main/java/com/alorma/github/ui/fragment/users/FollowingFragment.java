package com.alorma.github.ui.fragment.users;

import android.os.Bundle;

import com.alorma.github.R;
import com.alorma.github.sdk.services.user.UserFollowingClient;

/**
 * Created by Bernat on 13/07/2014.
 */
public class FollowingFragment extends BaseUsersListFragment {
    private String username;

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
        setAction(new UserFollowingClient(username));
    }

    @Override
    protected void executePaginatedRequest(int page) {
        setAction(new UserFollowingClient(username, page));
    }

    @Override
    protected void loadArguments() {
        if (getArguments() != null) {
            username = getArguments().getString(USERNAME);
        }
    }

    @Override
    protected int getNoDataText() {
        return R.string.no_followings;
    }
}

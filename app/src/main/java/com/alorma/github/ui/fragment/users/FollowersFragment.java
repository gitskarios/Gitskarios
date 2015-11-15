package com.alorma.github.ui.fragment.users;

import android.os.Bundle;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.services.client.GithubListClient;
import com.alorma.github.sdk.services.user.UserFollowersClient;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;

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
        super.executeRequest();
        setAction(new UserFollowersClient(getActivity(), username));
    }

    @Override
    protected void executePaginatedRequest(int page) {
        super.executePaginatedRequest(page);
        setAction(new UserFollowersClient(getActivity(), username, page));
    }

    @Override
    protected void loadArguments() {
        if (getArguments() != null) {
            username = getArguments().getString(USERNAME);
        }
    }

    @Override
    protected int getNoDataText() {
        return R.string.no_followers;
    }
}


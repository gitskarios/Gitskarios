package com.alorma.github.ui.fragment.repos;

import android.os.Bundle;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.services.repos.UserReposClient;
import com.alorma.github.ui.listeners.TitleProvider;
import com.alorma.github.utils.RepoUtils;
import com.alorma.gitskarios.core.Pair;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.List;

public class UsernameReposFragment extends BaseReposListFragment implements TitleProvider {

    private String username;

    public static UsernameReposFragment newInstance(String username) {
        UsernameReposFragment currentAccountReposFragment = new UsernameReposFragment();
        if (username != null) {
            Bundle bundle = new Bundle();
            bundle.putString(USERNAME, username);

            currentAccountReposFragment.setArguments(bundle);
        }
        return currentAccountReposFragment;
    }

    @Override
    public void onNext(Pair<List<Repo>, Integer> listIntegerPair) {
        super.onNext(listIntegerPair);

        if (getAdapter() != null) {
            getAdapter().showOwnerNameExtra(false);
        }
    }

    @Override
    protected void loadArguments() {
        if (getArguments() != null) {
            username = getArguments().getString(USERNAME);
        }
    }

    @Override
    protected void executeRequest() {
        super.executeRequest();

        if (username != null) {
            setAction(new UserReposClient(username, RepoUtils.sortOrder(getActivity())));
        }
    }

    @Override
    protected void executePaginatedRequest(int page) {
        super.executePaginatedRequest(page);
        if (username != null) {
            setAction(new UserReposClient(username, RepoUtils.sortOrder(getActivity()), page));
        }
    }

    @Override
    public int getTitle() {
        return R.string.repositories;
    }

    @Override
    public IIcon getTitleIcon() {
        return Octicons.Icon.oct_repo;
    }
}

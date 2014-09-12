package com.alorma.github.ui.fragment.repos;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.ListRepos;
import com.alorma.github.sdk.bean.info.PaginationLink;
import com.alorma.github.ui.loader.ReposLoader;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.util.Pair;

public class SyncReposFragment extends SyncBaseReposListFragment
        implements LoaderManager.LoaderCallbacks<Pair<PaginationLink, ListRepos>> {

    private static final String PAGE_LOADER_ARGUMENT = "page";

    private String mUserName;

    public static SyncReposFragment newInstance() {
        return new SyncReposFragment();
    }

    public static SyncReposFragment newInstance(String username) {
        SyncReposFragment reposFragment = new SyncReposFragment();
        if (username != null) {
            Bundle bundle = new Bundle();
            bundle.putString(USERNAME, username);

            reposFragment.setArguments(bundle);
        }
        return reposFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserName = getArguments().getString(USERNAME);
        }
    }

    @Override
    protected void loadArguments() {
    }

    @Override
    protected void executePaginatedRequest(int page) {
        super.executePaginatedRequest(page);

        final Bundle bundle = new Bundle();
        bundle.putInt(PAGE_LOADER_ARGUMENT, page);
        getLoaderManager().restartLoader(0, bundle, this);
    }

    @Override
    protected int getNoDataText() {
        return R.string.no_repositories;
    }

    @Override
    public Loader<Pair<PaginationLink, ListRepos>> onCreateLoader(final int id, final Bundle args) {
        return new ReposLoader(getActivity(), mUserName, args.getInt(PAGE_LOADER_ARGUMENT));
    }

    @Override
    public void onLoaderReset(final Loader<Pair<PaginationLink, ListRepos>> loader) {
    }
}

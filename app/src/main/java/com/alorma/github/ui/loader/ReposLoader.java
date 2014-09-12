package com.alorma.github.ui.loader;

import com.alorma.github.sdk.bean.dto.response.ListRepos;
import com.alorma.github.sdk.bean.info.PaginationLink;
import com.alorma.github.sdk.services.repos.SyncUserReposClient;

import android.content.Context;
import android.util.Pair;

public class ReposLoader extends AsyncLoader<Pair<PaginationLink, ListRepos>> {

    private final SyncUserReposClient mClient;

    private final String mUsername;

    private final int mPage;

    public ReposLoader(final Context context, final String username, final int page) {
        super(context);

        mUsername = username;
        mPage = page;

        mClient = new SyncUserReposClient(context);
    }

    @Override
    public Pair<PaginationLink, ListRepos> loadInBackground() {
        if (mUsername == null) {
            return mClient.getUserRepos(mPage);
        }
        return mClient.getReposByUsername(mUsername, mPage);
    }

    @Override
    protected void releaseResources(final Pair<PaginationLink, ListRepos> data) {

    }
}
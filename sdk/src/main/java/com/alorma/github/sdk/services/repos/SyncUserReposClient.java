package com.alorma.github.sdk.services.repos;

import com.alorma.github.sdk.bean.dto.response.ListRepos;
import com.alorma.github.sdk.bean.info.PaginationLink;
import com.alorma.github.sdk.services.client.SyncBaseClient;

import android.content.Context;
import android.util.Pair;

import java.io.IOException;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class SyncUserReposClient extends SyncBaseClient {

    private final SyncReposService mReposService;

    public SyncUserReposClient(final Context context) {
        super(context);

        mReposService = mRestAdapter.create(SyncReposService.class);
    }

    public Pair<PaginationLink, ListRepos> getUserRepos(final int page) {
        Response response = mReposService.userReposList(page);
        try {
            final ListRepos repos = getObjectFromResponse(response, ListRepos.class);
            return new Pair<PaginationLink, ListRepos>(getLinkData(response), repos);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RetrofitError e) {
            e.printStackTrace();
        }
        return null;
    }

    public Pair<PaginationLink, ListRepos> getReposByUsername(final String username,
            final int page) {
        Response response = mReposService.userReposList(username, page);
        try {
            final ListRepos repos = getObjectFromResponse(response, ListRepos.class);
            return new Pair<PaginationLink, ListRepos>(getLinkData(response), repos);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RetrofitError e) {
            e.printStackTrace();
        }
        return null;
    }
}
package com.alorma.github.ui.fragment;

import android.os.Bundle;

import com.alorma.github.sdk.bean.dto.response.Gist;
import com.alorma.github.sdk.bean.dto.response.ListGists;
import com.alorma.github.sdk.services.gists.UserGistsClient;
import com.alorma.github.ui.adapter.gists.GistsAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;

import java.util.ArrayList;

public class GistsFragment extends PaginatedListFragment<ListGists> {

    private GistsAdapter gistsAdapter;
    private String username;

    public static GistsFragment newInstance() {
        return new GistsFragment();
    }

    public static GistsFragment newInstance(String username) {
        GistsFragment gistsFragment = new GistsFragment();
        if (username != null) {
            Bundle bundle = new Bundle();
            bundle.putString(USERNAME, username);

            gistsFragment.setArguments(bundle);
        }
        return gistsFragment;
    }

    @Override
    protected void executeRequest() {
        UserGistsClient client;

        if (getArguments() != null) {
            username = getArguments().getString(USERNAME);
        }

        client = new UserGistsClient(getActivity(), username);

        client.setOnResultCallback(this);
        client.execute();
    }

    @Override
    protected void onResponse(ListGists gists) {
        if (gistsAdapter == null) {
            setUpList();
        }
        gistsAdapter.addAll(gists);
    }

    private void setUpList() {
        gistsAdapter = new GistsAdapter(getActivity(), new ArrayList<Gist>());
        setListAdapter(gistsAdapter);
    }

    @Override
    protected void executePaginatedRequest(int page) {
        UserGistsClient client = new UserGistsClient(getActivity(), username, page);
        client.setOnResultCallback(this);
        client.execute();
    }
}

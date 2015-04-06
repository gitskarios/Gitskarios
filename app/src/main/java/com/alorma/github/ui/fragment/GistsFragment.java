package com.alorma.github.ui.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Gist;
import com.alorma.github.sdk.bean.dto.response.ListGists;
import com.alorma.github.sdk.services.gists.UserGistsClient;
import com.alorma.github.ui.activity.gists.CreateGistActivity;
import com.alorma.github.ui.adapter.GistsAdapter;
import com.alorma.github.ui.fragment.base.SecondaryPaginatedListFragment;
import com.alorma.githubicons.GithubIconify;

public class GistsFragment extends SecondaryPaginatedListFragment<ListGists, Gist> {

    private GistsAdapter gistsAdapter;
    public GistsFragmentListener gistsFragmentListener;
    private String username;

    public static GistsFragment newInstance(String username) {
        Bundle bundle = new Bundle();
        bundle.putString(USERNAME, username);

        GistsFragment f = new GistsFragment();
        f.setArguments(bundle);

        return f;
    }

    @Override
    protected void loadArguments() {
        if (getArguments() != null) {
            username = getArguments().getString(USERNAME);
        }
    }

    @Override
    protected GithubIconify.IconValue getNoDataIcon() {
        return GithubIconify.IconValue.octicon_gist;
    }

    @Override
    protected int getNoDataText() {
        return R.string.no_gists;
    }

    @Override
    protected void onListItemClick(Gist item) {
        if (gistsFragmentListener != null) {
            gistsFragmentListener.onGistsRequest(item);
        }
    }

    @Override
    protected void onResponse(ListGists gists, boolean refreshing) {
        if (gists != null && gists.size() > 0) {

            if (gistsAdapter == null || refreshing) {
                gistsAdapter = new GistsAdapter(getActivity(), gists);
                setListAdapter(gistsAdapter);
            }

            if (gistsAdapter.isLazyLoading()) {
                if (gistsAdapter != null) {
                    gistsAdapter.setLazyLoading(false);
                    gistsAdapter.addAll(gists);
                }
            } else {
                setListAdapter(gistsAdapter);
            }
        }
    }

    @Override
    protected void executeRequest() {
        super.executeRequest();

        UserGistsClient gistsClient = new UserGistsClient(getActivity(), username);
        gistsClient.setOnResultCallback(this);
        gistsClient.execute();
    }

    @Override
    protected void executePaginatedRequest(int page) {
        super.executePaginatedRequest(page);

        gistsAdapter.setLazyLoading(true);

        UserGistsClient gistsClient = new UserGistsClient(getActivity(), username, page);
        gistsClient.setOnResultCallback(this);
        gistsClient.execute();
    }

    @Override
    protected boolean useFAB() {
        return username == null;
    }

    public void setGistsFragmentListener(GistsFragmentListener gistsFragmentListener) {
        this.gistsFragmentListener = gistsFragmentListener;
    }

    public interface GistsFragmentListener {
        void onGistsRequest(Gist gist);
    }

    @Override
    protected void fabClick() {
        try {
            Intent intent = CreateGistActivity.createLauncherIntent(getActivity());
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
}
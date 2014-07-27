package com.alorma.github.ui.fragment.detail.repo;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Content;
import com.alorma.github.sdk.bean.dto.response.ListContents;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.repo.GetRepoContentsClient;
import com.alorma.github.ui.activity.FileActivity;
import com.alorma.github.ui.adapter.detail.repo.RepoContentAdapter;
import com.alorma.github.ui.listeners.RefreshListener;
import com.bugsense.trace.BugSenseHandler;

import java.util.ArrayList;
import java.util.Collections;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 20/07/2014.
 */
public class FilesTreeFragment extends ListFragment implements BaseClient.OnResultCallback<ListContents> {

    public static final String OWNER = "OWNER";
    public static final String REPO = "REPO";
    private String owner;
    private String repo;
    private RepoContentAdapter contentAdapter;
    private RefreshListener refreshListener;
    private Content currentContent = null;

    public static FilesTreeFragment newInstance(String owner, String repo, RefreshListener refreshListener) {
        Bundle bundle = new Bundle();
        bundle.putString(OWNER, owner);
        bundle.putString(REPO, repo);

        FilesTreeFragment f = new FilesTreeFragment();
        f.setRefreshListener(refreshListener);
        f.setArguments(bundle);
        return f;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.setBackgroundColor(getResources().getColor(R.color.gray_github));

        if (getArguments() != null) {
            owner = getArguments().getString(OWNER);
            repo = getArguments().getString(REPO);

            GetRepoContentsClient repoContentsClient = new GetRepoContentsClient(getActivity(), owner, repo);
            repoContentsClient.setOnResultCallback(this);
            repoContentsClient.execute();

            if (refreshListener != null) {
                refreshListener.showRefresh();
            }
        }
    }

    @Override
    public void onResponseOk(ListContents contents, Response r) {
        try {
            if (contentAdapter == null) {
                contentAdapter = new RepoContentAdapter(getActivity(), new ArrayList<Content>());
                setListAdapter(contentAdapter);
            }

            Collections.sort(contents, ListContents.SORT.TYPE);
            contentAdapter.clear();
            contentAdapter.addAll(contents);
        } catch (Exception e) {
            BugSenseHandler.addCrashExtraData("FilesTreeFragment", e.getMessage());
            BugSenseHandler.flush(getActivity());
        }
        if (refreshListener != null) {
            refreshListener.cancelRefresh();
        }
    }

    @Override
    public void onFail(RetrofitError error) {
        Log.e("FILES", "Error", error);
        if (refreshListener != null) {
            refreshListener.cancelRefresh();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (contentAdapter != null && contentAdapter.getCount() >= position) {
            Content item = contentAdapter.getItem(position);
            if (item.isDir()) {
                if (refreshListener != null) {
                    refreshListener.showRefresh();
                }
                GetRepoContentsClient repoContentsClient = new GetRepoContentsClient(getActivity(), owner, repo, item.path);
                repoContentsClient.setOnResultCallback(this);
                repoContentsClient.execute();
            } else {
                String url = item._links.html;
                Intent intent = FileActivity.createLauncherIntent(getActivity(), url);
                startActivity(intent);
            }
        }
    }

    public RefreshListener getRefreshListener() {
        return refreshListener;
    }

    public void setRefreshListener(RefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }
}

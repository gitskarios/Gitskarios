package com.alorma.github.ui.fragment.issues;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.alorma.github.sdk.bean.dto.response.ListIssues;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.issues.GetIssuesClient;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.adapter.issues.IssuesAdapter;
import com.alorma.github.ui.fragment.base.BaseListFragment;
import com.alorma.github.ui.listeners.RefreshListener;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 22/08/2014.
 */
public class IssuesFragment extends BaseListFragment implements BaseClient.OnResultCallback<ListIssues> {

    private RefreshListener refreshListener;
    private String owner;
    private String repository;

    public static IssuesFragment newInstance(String owner, String repo, RefreshListener listener) {
        Bundle bundle = new Bundle();
        bundle.putString("OWNER", owner);
        bundle.putString("REPO", repo);

        IssuesFragment fragment = new IssuesFragment();
        fragment.setRefreshListener(listener);
        fragment.setArguments(bundle);
        return fragment;
    }

    protected void executeRequest() {
        if (owner != null && repository != null) {

            if (refreshListener != null) {
                refreshListener.showRefresh();
            }

            GetIssuesClient issuesClient = new GetIssuesClient(getActivity(), owner, repository);
            issuesClient.setOnResultCallback(this);
            issuesClient.execute();
        }
    }

    @Override
    public void onResponseOk(ListIssues issues, Response response) {
        if (issues != null && issues.size() > 0) {

            if (refreshListener != null) {
                refreshListener.cancelRefresh();
            }

            IssuesAdapter adapter = new IssuesAdapter(getActivity(), issues);
            setListAdapter(adapter);
        }
    }

    @Override
    public void onFail(RetrofitError error) {
        ErrorHandler.onRetrofitError(getActivity(), "IssuesFragment", error);
    }

    public void setRefreshListener(RefreshListener listener) {
        this.refreshListener = listener;
    }

    @Override
    protected void loadArguments() {
        if (getArguments() != null) {
            this.owner = getArguments().getString("OWNER");
            this.repository = getArguments().getString("REPO");
        }
        executeRequest();
    }
}

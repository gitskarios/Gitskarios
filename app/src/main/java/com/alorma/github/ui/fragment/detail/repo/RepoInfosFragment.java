package com.alorma.github.ui.fragment.detail.repo;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.alorma.github.BuildConfig;
import com.alorma.github.sdk.bean.dto.response.ListBranches;
import com.alorma.github.sdk.bean.dto.response.ListContributors;
import com.alorma.github.sdk.bean.dto.response.ListIssues;
import com.alorma.github.sdk.bean.dto.response.ListReleases;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.repo.BaseRepoClient;
import com.alorma.github.sdk.services.repo.GetRepoBranchesClient;
import com.alorma.github.sdk.services.repo.GetRepoContributorsClient;
import com.alorma.github.sdk.services.repo.GetRepoIssuesClient;
import com.alorma.github.sdk.services.repo.GetRepoReleasesClient;
import com.alorma.github.ui.listeners.RefreshListener;
import com.bugsense.trace.BugSenseHandler;
import com.joanzapata.android.iconify.Iconify;

import java.util.concurrent.LinkedBlockingQueue;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 27/07/2014.
 */
public class RepoInfosFragment extends Fragment {

    public static final String OWNER = "OWNER";
    public static final String REPO = "REPO";

    private LinkedBlockingQueue<BaseRepoClient> clients;
    private String owner;
    private String repo;
    private RefreshListener refreshListener;

    public static Fragment newInstance(String owner, String repo, RefreshListener listener) {
        Bundle bundle = new Bundle();
        bundle.putString(OWNER, owner);
        bundle.putString(REPO, repo);

        RepoInfosFragment f = new RepoInfosFragment();
        f.setRefreshListener(listener);
        f.setArguments(bundle);
        return f;
    }

    private void configureClients() {

        GetRepoContributorsClient repoContributorsClient = new GetRepoContributorsClient(getActivity(), this.owner, this.repo);
        repoContributorsClient.setOnResultCallback(new ContributorsCallback());

        clients.add(repoContributorsClient);

        GetRepoBranchesClient repoBranchesClient = new GetRepoBranchesClient(getActivity(), this.owner, this.repo);
        repoBranchesClient.setOnResultCallback(new BranchesCallback());

        clients.add(repoBranchesClient);

        GetRepoReleasesClient repoReleasesClient = new GetRepoReleasesClient(getActivity(), this.owner, this.repo);
        repoReleasesClient.setOnResultCallback(new ReleasesCallback());

        clients.add(repoReleasesClient);

        GetRepoIssuesClient repoIssuesClient = new GetRepoIssuesClient(getActivity(), this.owner, this.repo);
        repoIssuesClient.setOnResultCallback(new IssuesCallback());

        clients.add(repoIssuesClient);
    }

    public void setRefreshListener(RefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    private class ContributorsCallback implements BaseClient.OnResultCallback<ListContributors> {
        @Override
        public void onResponseOk(ListContributors contributors, Response r) {
            if (contributors != null) {
                Iconify.IconValue iconValue = Iconify.IconValue.fa_group;
                if (contributors.size() == 1) {
                    iconValue = Iconify.IconValue.fa_user;
                }
                // TODO set contributors num
            }
            executeNextClient();
        }

        @Override
        public void onFail(RetrofitError error) {
            onError("CONTRIBUTORS", error, true);
        }
    }

    private class BranchesCallback implements BaseClient.OnResultCallback<ListBranches> {
        @Override
        public void onResponseOk(ListBranches branches, Response r) {
            if (branches != null) {
                // TODO set branches num
            }
            executeNextClient();
        }

        @Override
        public void onFail(RetrofitError error) {
            onError("BRANCHES", error, true);
        }
    }

    private class ReleasesCallback implements BaseClient.OnResultCallback<ListReleases> {
        @Override
        public void onResponseOk(ListReleases releases, Response r) {
            if (releases != null) {
                // TODO set releases num
            }
            executeNextClient();
        }

        @Override
        public void onFail(RetrofitError error) {
            onError("RELEASES", error, true);
        }
    }

    private class IssuesCallback implements BaseClient.OnResultCallback<ListIssues> {
        @Override
        public void onResponseOk(ListIssues issues, Response r) {
            if (issues != null) {
                // TODO set issues num
            }
            executeNextClient();
        }

        @Override
        public void onFail(RetrofitError error) {
            onError("ISSUES", error, true);
        }
    }

    private void onError(String tag, RetrofitError error, boolean executeNext) {
        Log.e(tag, "Error", error);
        if (BuildConfig.DEBUG) {
            Toast.makeText(getActivity(), "Error: " + tag, Toast.LENGTH_SHORT).show();
        }
        if (executeNext) {
            executeNextClient();
        }

        if (error != null && error.getMessage() != null) {
            BugSenseHandler.addCrashExtraData(tag, error.getMessage());
            BugSenseHandler.flush(getActivity());
        }
    }

    private void executeNextClient() {
        if (clients != null) {
            BaseRepoClient poll = clients.poll();
            if (poll != null) {
                poll.execute();
            }
        }
    }
}

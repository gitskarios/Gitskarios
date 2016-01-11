package com.alorma.github.ui.fragment.releases;

import android.os.Bundle;
import android.view.LayoutInflater;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Release;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.GithubListClient;
import com.alorma.github.sdk.services.repo.GetRepoReleasesClient;
import com.alorma.github.ui.adapter.ReleasesAdapter;
import com.alorma.github.ui.fragment.base.LoadingListFragment;
import com.alorma.github.ui.listeners.TitleProvider;
import com.alorma.gitskarios.core.Pair;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RepoReleasesFragment extends LoadingListFragment<ReleasesAdapter> implements TitleProvider, Observer<List<Release>> {

    private static final String REPO_INFO = "REPO_INFO";
    private static final String REPO_PERMISSIONS = "REPO_PERMISSIONS";

    private RepoInfo repoInfo;

    public static RepoReleasesFragment newInstance(RepoInfo info) {
        RepoReleasesFragment repoReleasesFragment = new RepoReleasesFragment();

        Bundle args = new Bundle();

        args.putParcelable(REPO_INFO, info);

        repoReleasesFragment.setArguments(args);

        return repoReleasesFragment;
    }

    @Override
    protected void loadArguments() {
        repoInfo = (RepoInfo) getArguments().getParcelable(REPO_INFO);
    }

    @Override
    protected void executeRequest() {
        super.executeRequest();

        if (getAdapter() != null) {
            getAdapter().clear();
        }

        setAction(new GetRepoReleasesClient(repoInfo, 0));
    }

    @Override
    protected void executePaginatedRequest(int page) {
        super.executePaginatedRequest(page);

        setAction(new GetRepoReleasesClient(repoInfo, page));
    }

    private void setAction(GithubListClient<List<Release>> getRepoReleasesClient) {
        getRepoReleasesClient.observable().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Pair<List<Release>, Integer>, List<Release>>() {
                    @Override
                    public List<Release> call(Pair<List<Release>, Integer> listIntegerPair) {
                        setPage(listIntegerPair.second);
                        return listIntegerPair.first;
                    }
                })
                .subscribe(this);
    }

    @Override
    public void onNext(List<Release> releases) {
        if (releases.size() > 0) {
            hideEmpty();
            if (refreshing || getAdapter() == null) {
                ReleasesAdapter adapter = new ReleasesAdapter(LayoutInflater.from(getActivity()), repoInfo);
                adapter.addAll(releases);
                setAdapter(adapter);
            } else {
                getAdapter().addAll(releases);
            }
        } else if (getAdapter() == null || getAdapter().getItemCount() == 0) {
            setEmpty();
        } else {
            getAdapter().clear();
            setEmpty();
        }
    }

    @Override
    public void onCompleted() {
        stopRefresh();
    }

    @Override
    public void onError(Throwable error) {
        stopRefresh();
        if (getAdapter() == null || getAdapter().getItemCount() == 0) {
            setEmpty();
        }
    }

    @Override
    protected Octicons.Icon getNoDataIcon() {
        return null;
    }

    @Override
    protected int getNoDataText() {
        return R.string.no_releases;
    }

    @Override
    public int getTitle() {
        return R.string.releases;
    }

    @Override
    public IIcon getTitleIcon() {
        return Octicons.Icon.oct_tag;
    }
}

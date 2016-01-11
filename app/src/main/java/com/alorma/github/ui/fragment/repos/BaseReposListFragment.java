package com.alorma.github.ui.fragment.repos;

import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;

import com.alorma.github.GitskariosSettings;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.services.client.GithubListClient;
import com.alorma.github.ui.adapter.repos.ReposAdapter;
import com.alorma.github.ui.fragment.base.LoadingListFragment;
import com.alorma.gitskarios.core.Pair;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.Collection;
import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Bernat on 17/07/2014.
 */
public abstract class BaseReposListFragment extends LoadingListFragment<ReposAdapter>
        implements SharedPreferences.OnSharedPreferenceChangeListener, Observer<Pair<List<Repo>, Integer>> {

    private GitskariosSettings settings;

    @Override
    public void onCompleted() {
        stopRefresh();
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        Snackbar.make(recyclerView, R.string.error_loading_repos, Snackbar.LENGTH_SHORT).show();
        stopRefresh();
    }

    @Override
    public void onNext(Pair<List<Repo>, Integer> listIntegerPair) {
        setPage(listIntegerPair.second);
        List<Repo> repos = listIntegerPair.first;

        if (repos.size() > 0) {
            hideEmpty();
            if (refreshing || getAdapter() == null) {
                setUpList(repos);
            } else {
                getAdapter().addAll(repos);
            }
        } else if (getAdapter() == null || getAdapter().getItemCount() == 0) {
            setEmpty();
        } else {
            getAdapter().clear();
            setEmpty();
        }
    }

    protected void setUpList(Collection<Repo> repos) {
        settings = new GitskariosSettings(getActivity());
        settings.registerListener(this);

        ReposAdapter reposAdapter = new ReposAdapter(LayoutInflater.from(getActivity()));
        reposAdapter.showOwnerName(showAdapterOwnerName());
        reposAdapter.addAll(repos);

        setAdapter(reposAdapter);
    }

    protected boolean showAdapterOwnerName() {
        return false;
    }

    @Override
    public void setEmpty(boolean withError, int statusCode) {
        super.setEmpty(withError, statusCode);
        if (getAdapter() != null) {
            getAdapter().clear();
        }
    }

    protected void setAction(GithubListClient<List<Repo>> reposClient) {
        startRefresh();
        reposClient.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this);
    }

    @Override
    protected Octicons.Icon getNoDataIcon() {
        return Octicons.Icon.oct_repo;
    }

    @Override
    protected int getNoDataText() {
        return 0;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (GitskariosSettings.KEY_REPO_SORT.equals(key)) {
            executeRequest();
        }
    }

    @Override
    public void onDestroy() {
        if (settings != null) {
            settings.unregisterListener(this);
        }
        super.onDestroy();
    }
}

package com.alorma.github.ui.fragment.repos;

import android.content.SharedPreferences;
import android.view.LayoutInflater;

import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.utils.GitskariosSettings;
import com.alorma.github.ui.adapter.repos.ReposAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.Collection;
import java.util.List;

import retrofit.RetrofitError;

/**
 * Created by Bernat on 17/07/2014.
 */
public abstract class BaseReposListFragment extends PaginatedListFragment<List<Repo>, ReposAdapter> implements SharedPreferences.OnSharedPreferenceChangeListener {

    private GitskariosSettings settings;

    @Override
    protected void onResponse(List<Repo> repos, boolean refreshing) {
        if (repos.size() > 0) {
            hideEmpty();
            if (getAdapter() != null) {
                getAdapter().addAll(repos);
            } else {
                setUpList(repos);
            }
        } else if (getAdapter() == null || getAdapter().getItemCount() == 0) {
            setEmpty(false);
        }
    }

    @Override
    public void onFail(RetrofitError error) {
        super.onFail(error);
        if (getAdapter() == null || getAdapter().getItemCount() == 0) {
            if (error != null && error.getResponse() != null) {
                setEmpty(true, error.getResponse().getStatus());
            }
        }
    }

    protected void setUpList(Collection<Repo> repos) {
        settings = new GitskariosSettings(getActivity());
        settings.registerListener(this);

        ReposAdapter reposAdapter = new ReposAdapter(LayoutInflater.from(getActivity()));
        reposAdapter.addAll(repos);

        setAdapter(reposAdapter);
    }

    @Override
    public void setEmpty(boolean withError, int statusCode) {
        super.setEmpty(withError, statusCode);
        if (getAdapter() != null) {
            getAdapter().clear();
        }
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

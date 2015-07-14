package com.alorma.github.ui.fragment.repos;

import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.alorma.github.UrlsManager;
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

    protected ReposAdapter reposAdapter;
    private GitskariosSettings settings;

   /* @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (reposAdapter != null && reposAdapter.getItemCount() >= position) {
            Repo item = reposAdapter.getItem(position);

        }
    }*/

    @Override
    protected void onResponse(List<Repo> repos, boolean refreshing) {
        if (repos.size() > 0) {
            hideEmpty();
            if (getAdapter() != null) {
                reposAdapter.addAll(repos);
            } else if (reposAdapter == null) {
                setUpList(repos);
            } else {
                setAdapter(reposAdapter);
            }
        } else if (reposAdapter == null || reposAdapter.getItemCount() == 0) {
            setEmpty();
        }
    }

    @Override
    public void onFail(RetrofitError error) {
        super.onFail(error);
        if (reposAdapter == null || reposAdapter.getItemCount() == 0) {
            if (error != null && error.getResponse() != null) {
                setEmpty(error.getResponse().getStatus());
            }
        }
    }

    protected void setUpList(Collection<Repo> repos) {
        settings = new GitskariosSettings(getActivity());
        settings.registerListener(this);

        reposAdapter = new ReposAdapter(LayoutInflater.from(getActivity()));
        reposAdapter.addAll(repos);

        setAdapter(reposAdapter);
    }

    @Override
    public void setEmpty(int statusCode) {
        super.setEmpty(statusCode);
        if (reposAdapter != null) {
            reposAdapter.clear();
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

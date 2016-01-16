package com.alorma.github.ui.fragment.users;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.services.client.GithubListClient;
import com.alorma.github.ui.adapter.users.UsersAdapter;
import com.alorma.github.ui.fragment.base.LoadingListFragment;
import com.alorma.gitskarios.core.Pair;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Bernat on 13/07/2014.
 */
public abstract class BaseUsersListFragment extends LoadingListFragment<UsersAdapter> implements Observer<Pair<List<User>, Integer>> {

    @Override
    public void onNext(Pair<List<User>, Integer> listIntegerPair) {
        setPage(listIntegerPair.second);
        List<User> users = listIntegerPair.first;

        if (users.size() > 0) {
            hideEmpty();
            if (refreshing || getAdapter() == null) {
                UsersAdapter adapter = new UsersAdapter(LayoutInflater.from(getActivity()));
                adapter.addAll(users);
                setAdapter(adapter);
            } else {
                getAdapter().addAll(users);
            }
        } else if (getAdapter() == null || getAdapter().getItemCount() == 0) {
            setEmpty();
        } else {
            getAdapter().clear();
            setEmpty();
        }
    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onCompleted() {
        stopRefresh();
    }

    protected void setAction(GithubListClient<List<User>> userFollowersClient) {
        startRefresh();
        userFollowersClient.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this);
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.grid_layout_columns));
    }

    @Override
    protected Octicons.Icon getNoDataIcon() {
        return Octicons.Icon.oct_octoface;
    }
}


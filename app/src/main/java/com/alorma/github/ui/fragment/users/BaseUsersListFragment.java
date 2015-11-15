package com.alorma.github.ui.fragment.users;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.services.client.GithubListClient;
import com.alorma.github.ui.adapter.users.UsersAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.mikepenz.octicons_typeface_library.Octicons;
import java.util.List;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Bernat on 13/07/2014.
 */
public abstract class BaseUsersListFragment extends PaginatedListFragment<UsersAdapter>
implements Observer<Pair<List<User>, Integer>>{

    @Override
    public void onNext(Pair<List<User>, Integer> listIntegerPair) {
        setPage(listIntegerPair.second);
        List<User> users = listIntegerPair.first;
        if (users.size() > 0) {
            hideEmpty();
            if (getAdapter() != null) {
                getAdapter().addAll(users);
            } else {
                UsersAdapter adapter = new UsersAdapter(LayoutInflater.from(getActivity()));
                adapter.addAll(users);
                setAdapter(adapter);
            }
        } else if (getAdapter() == null || getAdapter().getItemCount() == 0) {
            setEmpty(false);
        }
    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onCompleted() {

    }

    protected void setAction(GithubListClient<List<User>> userFollowersClient) {
        userFollowersClient.observable().observeOn(AndroidSchedulers.mainThread())
            .subscribe(this);
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.grid_layout_columns));
    }

    @Override
    protected RecyclerView.ItemDecoration getItemDecoration() {
        return null;
    }

    @Override
    protected Octicons.Icon getNoDataIcon() {
        return Octicons.Icon.oct_octoface;
    }

}


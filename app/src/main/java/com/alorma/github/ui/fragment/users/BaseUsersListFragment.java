package com.alorma.github.ui.fragment.users;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.ui.adapter.users.UsersAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.List;

import retrofit.RetrofitError;

/**
 * Created by Bernat on 13/07/2014.
 */
public abstract class BaseUsersListFragment extends PaginatedListFragment<List<User>, UsersAdapter> {

    @Override
    protected void onResponse(List<User> users, boolean refreshing) {
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
            setEmpty();
        }
    }

    @Override
    public void onFail(RetrofitError error) {
        super.onFail(error);
        if (getAdapter() == null || getAdapter().getItemCount() == 0) {
        setEmpty();
        }
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


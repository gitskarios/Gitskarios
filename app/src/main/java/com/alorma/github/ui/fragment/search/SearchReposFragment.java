package com.alorma.github.ui.fragment.search;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.bean.dto.response.search.ReposSearch;
import com.alorma.github.sdk.services.search.RepoSearchClient;
import com.alorma.github.ui.adapter.repos.ReposAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;

import java.util.ArrayList;

/**
 * Created by Bernat on 08/08/2014.
 */
public class SearchReposFragment extends PaginatedListFragment<ReposSearch> {

    private String query = null;
    private ReposAdapter reposAdapter;
    private OnSearchReposListener onSearchReposListener;

    public static SearchReposFragment newInstance() {
        return new SearchReposFragment();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.setBackgroundColor(Color.WHITE);

        if (swipe != null) {
            swipe.setEnabled(false);
        }
    }

    @Override
    protected void executeRequest() {
        if (query != null) {
            RepoSearchClient client = new RepoSearchClient(getActivity(), query);
            client.setOnResultCallback(this);
            client.execute();
        }
    }

    @Override
    protected void executePaginatedRequest(int page) {
        if (query != null) {
            RepoSearchClient client = new RepoSearchClient(getActivity(), query, page);
            client.setOnResultCallback(this);
            client.execute();
        }
    }

    @Override
    protected void onQueryFail() {

    }

    @Override
    protected void onResponse(ReposSearch reposSearch) {
        if (reposAdapter == null) {
            reposAdapter = new ReposAdapter(getActivity(), new ArrayList<Repo>());
            setListAdapter(reposAdapter);
        }

        reposAdapter.addAll(reposSearch.items);
    }

    public void setQuery(String query) {
        this.query = query;
        if (getActivity() != null && isAdded()) {
            executeRequest();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (reposAdapter != null && reposAdapter.getCount() >= position) {
            Repo item = reposAdapter.getItem(position);
            if (onSearchReposListener != null) {
                onSearchReposListener.onRepoItemSelected(item);
            }
        }
    }

    public void setOnSearchReposListener(OnSearchReposListener onSearchReposListener) {
        this.onSearchReposListener = onSearchReposListener;
    }

    public interface OnSearchReposListener {
        void onRepoItemSelected(Repo repo);
    }
}

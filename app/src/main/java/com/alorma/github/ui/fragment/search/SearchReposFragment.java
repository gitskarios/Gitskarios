package com.alorma.github.ui.fragment.search;

import android.app.SearchManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.services.search.RepoSearchClient;
import com.alorma.github.ui.adapter.repos.ReposAdapter;
import com.alorma.github.ui.fragment.repos.BaseReposListFragment;
import com.alorma.githubicons.GithubIconify;

/**
 * Created by Bernat on 08/08/2014.
 */
public class SearchReposFragment extends BaseReposListFragment {

	private String query = null;
	private ReposAdapter reposAdapter;
	private OnSearchReposListener onSearchReposListener;

	public static SearchReposFragment newInstance(String query) {
		Bundle args = Bundle.EMPTY;
		args.putString(SearchManager.QUERY, query);
		SearchReposFragment f = new SearchReposFragment();
		f.setArguments(args);
		return f;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		view.setBackgroundColor(Color.WHITE);

		String query = getArguments().getString(SearchManager.QUERY);
		setQuery(query);
	}

	@Override
	protected GithubIconify.IconValue getNoDataIcon() {
		return GithubIconify.IconValue.octicon_repo;
	}

	@Override
	protected int getNoDataText() {
		return R.string.no_results;
	}

	@Override
	protected void loadArguments() {

	}

	@Override
	protected void executeRequest() {
		if (getActivity() != null) {
			if (query != null) {
				super.executeRequest();
				RepoSearchClient client = new RepoSearchClient(getActivity(), query);
				client.setOnResultCallback(this);
				client.execute();
			}
		}
	}

	@Override
	protected void executePaginatedRequest(int page) {
		if (getActivity() != null) {
			if (query != null) {
				super.executePaginatedRequest(page);
				RepoSearchClient client = new RepoSearchClient(getActivity(), query, page);
				client.setOnResultCallback(this);
				client.execute();
			}
		}
	}

	public void setQuery(String query) {
		this.query = query;
		executeRequest();
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

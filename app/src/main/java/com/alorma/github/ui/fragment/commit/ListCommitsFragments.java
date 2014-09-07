package com.alorma.github.ui.fragment.commit;

import android.os.Bundle;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.ListCommit;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.commit.ListCommitsClient;
import com.alorma.github.ui.adapter.CommitsAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.alorma.github.ui.listeners.RefreshListener;
import com.joanzapata.android.iconify.Iconify;

/**
 * Created by Bernat on 07/09/2014.
 */
public class ListCommitsFragments extends PaginatedListFragment<ListCommit> {
	private static final String OWNER = "OWNER";
	private static final String REPO = "REPO";
	private RepoInfo info;
	private CommitsAdapter commitsAdapter;

	public static ListCommitsFragments newInstance(String owner, String repo, RefreshListener listener) {
		Bundle bundle = new Bundle();
		bundle.putString(OWNER, owner);
		bundle.putString(REPO, repo);

		ListCommitsFragments fragment = new ListCommitsFragments();
		fragment.setRefreshListener(listener);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	protected void onResponse(ListCommit commits, boolean refreshing) {
		if (commits != null && commits.size() > 0) {

			if (commitsAdapter == null || refreshing) {
				commitsAdapter = new CommitsAdapter(getActivity(), commits);
				setListAdapter(commitsAdapter);
			}

			if (commitsAdapter.isLazyLoading()) {
				if (commitsAdapter != null) {
					commitsAdapter.setLazyLoading(false);
					commitsAdapter.addAll(commits);
				}
			}
		}
	}

	@Override
	protected void executeRequest() {
		super.executeRequest();
		ListCommitsClient client = new ListCommitsClient(getActivity(), info, 0);
		client.setOnResultCallback(this);
		client.execute();
	}

	@Override
	protected void executePaginatedRequest(int page) {
		super.executePaginatedRequest(page);
		ListCommitsClient client = new ListCommitsClient(getActivity(), info, page);
		client.setOnResultCallback(this);
		client.execute();
	}

	@Override
	protected void loadArguments() {
		info = new RepoInfo();
		if (getArguments() != null) {
			info.owner = getArguments().getString(OWNER);
			info.repo = getArguments().getString(REPO);
		}
	}

	@Override
	protected Iconify.IconValue getNoDataIcon() {
		return Iconify.IconValue.fa_code_fork;
	}

	@Override
	protected int getNoDataText() {
		return R.string.no_commits;
	}

	@Override
	protected boolean useInnerSwipeRefresh() {
		return false;
	}
}

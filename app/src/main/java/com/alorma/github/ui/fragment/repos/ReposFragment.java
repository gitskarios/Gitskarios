package com.alorma.github.ui.fragment.repos;

import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.alorma.github.R;
import com.alorma.github.sdk.services.repos.BaseReposClient;
import com.alorma.github.sdk.services.repos.UserReposClient;

public class ReposFragment extends BaseReposListFragment {

	private static final int NEW_REPO_REQUEST = 8978;
	private String username;
	private float fabOldY;
	private float fabNewY;

	public static ReposFragment newInstance() {
		return new ReposFragment();
	}

	public static ReposFragment newInstance(String username) {
		ReposFragment reposFragment = new ReposFragment();
		if (username != null) {
			Bundle bundle = new Bundle();
			bundle.putString(USERNAME, username);

			reposFragment.setArguments(bundle);
		}
		return reposFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			username = getArguments().getString(USERNAME);
		}
	}

	@Override
	protected void loadArguments() {

	}

	@Override
	protected void executeRequest() {
		super.executeRequest();
		BaseReposClient client = new UserReposClient(getActivity(), username);
		client.setOnResultCallback(this);
		client.execute();
	}

	@Override
	protected void executePaginatedRequest(int page) {
		super.executePaginatedRequest(page);
		UserReposClient client = new UserReposClient(getActivity(), username, page);
		client.setOnResultCallback(this);
		client.execute();
	}

	@Override
	protected int getNoDataText() {
		return R.string.no_repositories;
	}

}

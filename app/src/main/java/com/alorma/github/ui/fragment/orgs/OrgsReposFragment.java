package com.alorma.github.ui.fragment.orgs;

import android.os.Bundle;

import com.alorma.github.R;
import com.alorma.github.sdk.services.repos.BaseReposClient;
import com.alorma.github.sdk.services.repos.OrgsReposClient;
import com.alorma.github.sdk.services.repos.UserReposClient;
import com.alorma.github.ui.fragment.repos.BaseReposListFragment;

public class OrgsReposFragment extends BaseReposListFragment {

	private String org;

	private static final String ORGANIZATION = USERNAME;

	public static OrgsReposFragment newInstance() {
		return new OrgsReposFragment();
	}

	public static OrgsReposFragment newInstance(String username) {
		OrgsReposFragment reposFragment = new OrgsReposFragment();
		if (username != null) {
			Bundle bundle = new Bundle();
			bundle.putString(ORGANIZATION, username);

			reposFragment.setArguments(bundle);
		}
		return reposFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			org = getArguments().getString(ORGANIZATION);
		}
	}

	@Override
	protected void loadArguments() {

	}

	@Override
	protected void executeRequest() {
		super.executeRequest();
		BaseReposClient client;
		client = new OrgsReposClient(getActivity(), org);

		client.setOnResultCallback(this);
		client.execute();
	}

	@Override
	protected void executePaginatedRequest(int page) {
		super.executePaginatedRequest(page);
		OrgsReposClient client = new OrgsReposClient(getActivity(), org, page);
		client.setOnResultCallback(this);
		client.execute();
	}

	@Override
	protected int getNoDataText() {
		return R.string.no_repositories;
	}
}

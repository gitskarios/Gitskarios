package com.alorma.github.ui.fragment.orgs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.services.repos.BaseReposClient;
import com.alorma.github.sdk.services.orgs.OrgsReposClient;
import com.alorma.github.sdk.services.repos.GithubReposClient;
import com.alorma.github.ui.activity.RepoDetailActivity;
import com.alorma.github.ui.fragment.repos.BaseReposListFragment;

public class OrgsReposFragment extends BaseReposListFragment {

	private String org;

	private static final String ORGANIZATION = "ORG";

	public static OrgsReposFragment newInstance() {
		return new OrgsReposFragment();
	}

	public static OrgsReposFragment newInstance(String orgName) {
		OrgsReposFragment reposFragment = new OrgsReposFragment();
		if (orgName != null) {
			Bundle bundle = new Bundle();
			bundle.putString(ORGANIZATION, orgName);

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
		GithubReposClient client = new OrgsReposClient(getActivity(), org);

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

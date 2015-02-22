package com.alorma.github.ui.fragment.orgs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.ListOrganizations;
import com.alorma.github.sdk.bean.dto.response.Organization;
import com.alorma.github.sdk.services.orgs.GetOrgsClient;
import com.alorma.github.ui.activity.OrganizationActivity;
import com.alorma.github.ui.adapter.orgs.OrganizationsAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.alorma.githubicons.GithubIconify;

/**
 * Created by Bernat on 13/07/2014.
 */
public class OrganizationsFragment extends PaginatedListFragment<ListOrganizations> {
	private String username;
	private OrganizationsAdapter adapter;

	public static OrganizationsFragment newInstance() {
		return new OrganizationsFragment();
	}

	public static OrganizationsFragment newInstance(String username) {
		OrganizationsFragment followersFragment = new OrganizationsFragment();
		if (username != null) {
			Bundle bundle = new Bundle();
			bundle.putString(USERNAME, username);

			followersFragment.setArguments(bundle);
		}
		return followersFragment;
	}

	@Override
	protected void executeRequest() {
		super.executeRequest();
		GetOrgsClient client = new GetOrgsClient(getActivity(), username);
		client.setOnResultCallback(this);
		client.execute();
	}

	@Override
	protected void executePaginatedRequest(int page) {
		super.executePaginatedRequest(page);
		GetOrgsClient client = new GetOrgsClient(getActivity(), username, page);
		client.setOnResultCallback(this);
		client.execute();
	}

	@Override
	protected void onResponse(ListOrganizations organizations, boolean refreshing) {
		getListView().setDivider(null);
		if (getListAdapter() != null) {
			adapter.addAll(organizations, paging);
		} else if (adapter == null) {
			setUpList(organizations);
		} else {
			setListAdapter(adapter);
		}
	}

	private void setUpList(ListOrganizations organizations) {
		adapter = new OrganizationsAdapter(getActivity(), organizations);
		setListAdapter(adapter);
	}

	@Override
	protected void loadArguments() {
		if (getArguments() != null) {
			username = getArguments().getString(USERNAME);
		}
	}

	@Override
	protected GithubIconify.IconValue getNoDataIcon() {
		return GithubIconify.IconValue.octicon_organization;
	}

	@Override
	protected int getNoDataText() {
		return R.string.no_organizations;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Organization item = adapter.getItem(position);

		Intent intent = OrganizationActivity.launchIntent(getActivity(), item.login);
		startActivity(intent);
	}
}


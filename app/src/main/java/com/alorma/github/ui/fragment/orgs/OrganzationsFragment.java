package com.alorma.github.ui.fragment.orgs;

import android.os.Bundle;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.ListOrganizations;
import com.alorma.github.sdk.services.orgs.GetOrgsClient;
import com.alorma.github.sdk.services.repos.OrgsReposClient;
import com.alorma.github.sdk.services.user.UserFollowersClient;
import com.alorma.github.ui.adapter.orgs.OrganizationsAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.alorma.github.ui.fragment.users.BaseUsersListFragment;
import com.joanzapata.android.iconify.Iconify;

import retrofit.client.Response;

/**
 * Created by Bernat on 13/07/2014.
 */
public class OrganzationsFragment extends PaginatedListFragment<ListOrganizations> {
	private String username;
	private OrganizationsAdapter adapter;

	public static OrganzationsFragment newInstance() {
		return new OrganzationsFragment();
	}

	public static OrganzationsFragment newInstance(String username) {
		OrganzationsFragment followersFragment = new OrganzationsFragment();
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
		if (adapter == null) {
			adapter = new OrganizationsAdapter(getActivity(), organizations);
			setListAdapter(adapter);
		} else {
			adapter.addAll(organizations);
		}
	}

	@Override
	protected void loadArguments() {
		if (getArguments() != null) {
			username = getArguments().getString(USERNAME);
		}
	}

	@Override
	protected Iconify.IconValue getNoDataIcon() {
		return Iconify.IconValue.fa_group;
	}

	@Override
	protected int getNoDataText() {
		return R.string.no_organizations;
	}
}


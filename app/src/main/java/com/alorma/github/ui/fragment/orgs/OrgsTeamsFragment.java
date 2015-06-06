package com.alorma.github.ui.fragment.orgs;

import android.os.Bundle;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.ListTeams;
import com.alorma.github.sdk.bean.dto.response.ListUsers;
import com.alorma.github.sdk.services.orgs.OrgsMembersClient;
import com.alorma.github.sdk.services.orgs.teams.GetOrgTeamsClient;
import com.alorma.github.ui.adapter.TeamsAdapter;
import com.alorma.github.ui.adapter.users.UsersAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.alorma.github.ui.fragment.users.BaseUsersListFragment;
import com.mikepenz.octicons_typeface_library.Octicons;

/**
 * Created by Bernat on 13/07/2014.
 */
public class OrgsTeamsFragment extends PaginatedListFragment<ListTeams> {
	private String org;
	private TeamsAdapter teamsAdapter;

	public static OrgsTeamsFragment newInstance() {
		return new OrgsTeamsFragment();
	}

	public static OrgsTeamsFragment newInstance(String org) {
		OrgsTeamsFragment orgsMembersFragment = new OrgsTeamsFragment();
		if (org != null) {
			Bundle bundle = new Bundle();
			bundle.putString(USERNAME, org);

			orgsMembersFragment.setArguments(bundle);
		}
		return orgsMembersFragment;
	}

	@Override
	protected void executeRequest() {
		GetOrgTeamsClient client = new GetOrgTeamsClient(getActivity(), org);
		client.setOnResultCallback(this);
		client.execute();
	}

	@Override
	protected void executePaginatedRequest(int page) {
		GetOrgTeamsClient client = new GetOrgTeamsClient(getActivity(), org, page);
		client.setOnResultCallback(this);
		client.execute();
	}

	@Override
	protected void loadArguments() {
		if (getArguments() != null) {
			org = getArguments().getString(USERNAME);
		}
	}

	@Override
	protected Octicons.Icon getNoDataIcon() {
		return Octicons.Icon.oct_organization;
	}

	@Override
	protected int getNoDataText() {
		return R.string.org_no_teams;
	}

	@Override
	protected void onResponse(ListTeams teams, boolean refreshing) {
		getListView().setDivider(null);
		if (teams.size() > 0) {
			hideEmpty();
			if (getListAdapter() != null) {
				teamsAdapter.addAll(teams, paging);
			} else if (teamsAdapter == null) {
				setUpList(teams);
			} else {
				setListAdapter(teamsAdapter);
			}
		} else if (teamsAdapter == null || teamsAdapter.getCount() == 0) {
			setEmpty();
		}
	}

	protected TeamsAdapter setUpList(ListTeams teams) {
		teamsAdapter = new TeamsAdapter(getActivity(), teams);
		setListAdapter(teamsAdapter);
		return teamsAdapter;
	}
}

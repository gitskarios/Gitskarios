package com.alorma.github.ui.fragment.users;

import android.content.Intent;
import android.view.View;
import android.widget.ListView;

import com.alorma.github.sdk.bean.dto.response.ListUsers;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.ui.activity.ProfileActivity;
import com.alorma.github.ui.adapter.users.UsersAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.joanzapata.android.iconify.Iconify;

import java.util.ArrayList;

/**
 * Created by Bernat on 13/07/2014.
 */
public abstract class BaseUsersListFragment extends PaginatedListFragment<ListUsers> {

	private UsersAdapter usersAdapter;

	@Override
	protected void onResponse(ListUsers users, boolean refreshing) {
		if (usersAdapter == null) {
			setUpList();
		}
		usersAdapter.addAll(users, paging);
	}

	private void setUpList() {
		usersAdapter = new UsersAdapter(getActivity(), new ArrayList<User>());
		setListAdapter(usersAdapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		if (usersAdapter != null && usersAdapter.getItem(position) != null) {
			Intent launcherIntent = ProfileActivity.createLauncherIntent(getActivity(), usersAdapter.getItem(position));
			startActivity(launcherIntent);
		}
	}

	@Override
	protected Iconify.IconValue getNoDataIcon() {
		return Iconify.IconValue.fa_group;
	}
}


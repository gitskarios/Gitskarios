package com.alorma.github.ui.fragment.menu;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alorma.github.R;
import com.alorma.github.ui.adapter.MenuItemsAdapter;
import com.alorma.githubicons.GithubIconify;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bernat on 13/08/2014.
 */
public class MenuFragment extends Fragment implements MenuItemsAdapter.OnMenuItemSelectedListener {

	private OnMenuItemSelectedListener onMenuItemSelectedListener;
	private MenuItemsAdapter adapter;
	private int currentSelectedItemId;
	private MenuItem currentSelectedItem;

	public static MenuFragment newInstance() {
		return new MenuFragment();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.custom_list_fragment, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		currentSelectedItemId = 0;

		List<MenuItem> objMenuItems = new ArrayList<MenuItem>();
		objMenuItems.add(new MenuItem(0, 0, R.string.menu_my_profile, GithubIconify.IconValue.octicon_person));

		objMenuItems.add(new DividerMenuItem());

		objMenuItems.add(new MenuItem(0, 1, R.string.menu_organizations, GithubIconify.IconValue.octicon_organization));
		objMenuItems.add(new MenuItem(1, 1, R.string.menu_events, GithubIconify.IconValue.octicon_calendar));

		currentSelectedItem = new MenuItem(0, 2, R.string.navigation_repos, GithubIconify.IconValue.octicon_repo);
		objMenuItems.add(currentSelectedItem);
		objMenuItems.add(new MenuItem(1, 2, R.string.navigation_starred_repos, GithubIconify.IconValue.octicon_star));
		objMenuItems.add(new MenuItem(2, 2, R.string.navigation_watched_repos, GithubIconify.IconValue.octicon_eye));

		objMenuItems.add(new MenuItem(0, 3, R.string.navigation_followers, GithubIconify.IconValue.octicon_person));
		objMenuItems.add(new MenuItem(1, 3, R.string.navigation_following, GithubIconify.IconValue.octicon_person));

		objMenuItems.add(new DividerMenuItem());
		objMenuItems.add(new MenuItem(0, 4, R.string.navigation_settings, null));

		objMenuItems.add(new MenuItem(1, 4, R.string.navigation_about, null));

		if (onMenuItemSelectedListener != null) {
			onMenuItemSelectedListener.onMenuItemSelected(currentSelectedItem);
		}

		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

		adapter = new MenuItemsAdapter(getActivity(), objMenuItems);
		adapter.setOnMenuItemSelectedListener(this);
		recyclerView.setAdapter(adapter);
	}


	@Override
	public void onMenuItemSelected(MenuItem item) {
		if (item != null && onMenuItemSelectedListener != null) {
			currentSelectedItemId = item.id;
			switch (item.parentId) {
				case 0:
					itemCurrentUser(item);
					break;
				case 1:
					itemUser(item);
					break;
				case 2:
					itemRepositories(item);
					break;
				case 3:
					itemPeople(item);
					break;
				case 4:
					itemExtras(item);
					break;
			}
			onMenuItemSelectedListener.onMenuItemSelected(item);
		}
	}

	private void itemCurrentUser(MenuItem item) {
		onMenuItemSelectedListener.onProfileSelected();
	}

	private void itemUser(MenuItem item) {
		switch (item.id) {
			case 0:
				onMenuItemSelectedListener.onOrganizationsSelected();
				break;
			case 1:
				onMenuItemSelectedListener.onUserEventsSelected();
				break;
		}
	}

	private void itemRepositories(MenuItem item) {
		switch (item.id) {
			case 0:
				onMenuItemSelectedListener.onReposSelected();
				break;
			case 1:
				onMenuItemSelectedListener.onStarredSelected();
				break;
			case 2:
				onMenuItemSelectedListener.onWatchedSelected();
				break;
		}
	}

	private void itemPeople(MenuItem item) {
		switch (item.id) {
			case 0:
				onMenuItemSelectedListener.onFollowersSelected();
				break;
			case 1:
				onMenuItemSelectedListener.onFollowingSelected();
				break;
		}
	}

	private void itemExtras(MenuItem item) {
		switch (item.id) {
			case 0:
				onMenuItemSelectedListener.onSettingsSelected();
				break;
			case 1:
				onMenuItemSelectedListener.onAboutSelected();
				break;
		}
	}

	public void setOnMenuItemSelectedListener(OnMenuItemSelectedListener onMenuItemSelectedListener) {
		this.onMenuItemSelectedListener = onMenuItemSelectedListener;
	}

	public interface OnMenuItemSelectedListener {
		void onProfileSelected();

		void onReposSelected();

		void onStarredSelected();

		void onWatchedSelected();

		void onFollowersSelected();

		void onFollowingSelected();

		void onMenuItemSelected(@NonNull MenuItem item);

		void closeMenu();

		void onOrganizationsSelected();

		void onUserEventsSelected();

		void onSettingsSelected();

		void onAboutSelected();
	}
}

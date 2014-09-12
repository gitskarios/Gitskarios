package com.alorma.github.ui.fragment.menu;

import android.app.ListFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.alorma.github.R;
import com.alorma.github.ui.adapter.MenuItemsAdapter;
import com.joanzapata.android.iconify.Iconify;

import java.util.ArrayList;

/**
 * Created by Bernat on 13/08/2014.
 */
public class MenuFragment extends ListFragment {

    private static final String MENU_SELECTED_ITEM = "menu_selected_item";

    private static final int DEFAULT_SELECTED_POSITION = 3;

    private OnMenuItemSelectedListener onMenuItemSelectedListener;
	private MenuItemsAdapter adapter;
	private int currentSelectedPosition;

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

		adapter = new MenuItemsAdapter(getActivity(), new ArrayList<MenuItem>());

		setListAdapter(adapter);

		int color = getResources().getColor(R.color.accent);

		adapter.add(new CategoryMenuItem(-1, R.string.tab_user_parent, color, Iconify.IconValue.fa_group));
		adapter.add(new MenuItem(0, -1, R.string.menu_organizations, color, Iconify.IconValue.fa_group));

		adapter.add(new CategoryMenuItem(-2, R.string.tab_repos_parent, color, Iconify.IconValue.fa_code));
		adapter.add(new MenuItem(0, -2, R.string.navigation_repos, color, Iconify.IconValue.fa_code));
		adapter.add(new MenuItem(1, -2, R.string.navigation_starred_repos, color, Iconify.IconValue.fa_star));
		adapter.add(new MenuItem(2, -2, R.string.navigation_watched_repos, color, Iconify.IconValue.fa_eye));

		adapter.add(new CategoryMenuItem(-3, R.string.tab_people_parent, color, Iconify.IconValue.fa_code));

		adapter.add(new MenuItem(0, -3, R.string.navigation_followers, color, Iconify.IconValue.fa_user));
		adapter.add(new MenuItem(1, -3, R.string.navigation_following, color, Iconify.IconValue.fa_user));

                currentSelectedPosition = savedInstanceState == null ? DEFAULT_SELECTED_POSITION
                    : savedInstanceState.getInt(MENU_SELECTED_ITEM, DEFAULT_SELECTED_POSITION);

                if (onMenuItemSelectedListener != null) {
                    MenuItem item = adapter.getItem(currentSelectedPosition);
                    onMenuItemSelectedListener.onMenuItemSelected(item);
                }
        }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(MENU_SELECTED_ITEM, currentSelectedPosition);
    }

    @Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		MenuItem item = adapter.getItem(position);

		if (item != null && onMenuItemSelectedListener != null) {
			if (item.id > -1) {
				currentSelectedPosition = position;
				switch (item.parentId) {
					case -1:
						itemUser(item);
						break;
					case -2:
						itemRepositories(item);
						break;
					case -3:
						itemPeople(item);
						break;
				}
				onMenuItemSelectedListener.onMenuItemSelected(item);
			}
		}
	}

	private void itemUser(MenuItem item) {
		switch (item.id) {
			case 0:
				onMenuItemSelectedListener.onOrganizationsSelected();
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

	private boolean checkItem(MenuItem item) {
		return item != null && onMenuItemSelectedListener != null;
	}

	public void setOnMenuItemSelectedListener(OnMenuItemSelectedListener onMenuItemSelectedListener) {
		this.onMenuItemSelectedListener = onMenuItemSelectedListener;
	}

	public interface OnMenuItemSelectedListener {
		void onReposSelected();

		void onStarredSelected();

		void onWatchedSelected();

		void onFollowersSelected();

		void onFollowingSelected();

		void onMenuItemSelected(@NonNull MenuItem item);

		void closeMenu();

		void onOrganizationsSelected();
	}
}

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
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.user.GetAuthUserClient;
import com.alorma.github.ui.adapter.MenuItemsAdapter;
import com.alorma.github.ui.view.CircularImageView;
import com.alorma.github.utils.AttributesUtils;
import com.alorma.githubicons.GithubIconify;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 13/08/2014.
 */
public class MenuFragment extends Fragment implements MenuItemsAdapter.OnMenuItemSelectedListener, BaseClient.OnResultCallback<User>, View.OnClickListener {

	private OnMenuItemSelectedListener onMenuItemSelectedListener;
	private MenuItemsAdapter adapter;
	private MenuItem currentSelectedItem;
	private TextView userName;
	private TextView userLogin;
	private CircularImageView userAvatar;
	private View userLayout;

	public static MenuFragment newInstance() {
		return new MenuFragment();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.menu_fragment, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		userLayout = view.findViewById(R.id.user);
		userAvatar = (CircularImageView) view.findViewById(R.id.userAvatar);
		userLogin = (TextView) view.findViewById(R.id.userLogin);
		userName = (TextView) view.findViewById(R.id.userName);

		GetAuthUserClient authUserClient = new GetAuthUserClient(getActivity());
		authUserClient.setOnResultCallback(this);
		authUserClient.execute();

		userLayout.setOnClickListener(this);
		userAvatar.setOnClickListener(this);
		userLogin.setOnClickListener(this);
		userName.setOnClickListener(this);

		List<MenuItem> objMenuItems = new ArrayList<>();

		objMenuItems.add(new MenuItem(1, 1, R.string.menu_events, GithubIconify.IconValue.octicon_calendar));

		currentSelectedItem = new MenuItem(0, 2, R.string.navigation_repos, GithubIconify.IconValue.octicon_repo);
		objMenuItems.add(currentSelectedItem);
		objMenuItems.add(new MenuItem(1, 2, R.string.navigation_starred_repos, GithubIconify.IconValue.octicon_star));
		objMenuItems.add(new MenuItem(2, 2, R.string.navigation_watched_repos, GithubIconify.IconValue.octicon_eye));

		int primarColorPeople = AttributesUtils.getPrimaryColor(getActivity(), R.style.AppTheme_People);

		objMenuItems.add(new MenuItem(0, 3, R.string.navigation_people, GithubIconify.IconValue.octicon_person, primarColorPeople));

		objMenuItems.add(new DividerMenuItem());
		objMenuItems.add(new MenuItem(0, 4, R.string.navigation_settings, null));

		//objMenuItems.add(new MenuItem(1, 4, R.string.navigation_about, null));

		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

		adapter = new MenuItemsAdapter(getActivity(), objMenuItems);
		adapter.setOnMenuItemSelectedListener(this);
		recyclerView.setAdapter(adapter);

		if (onMenuItemSelectedListener != null) {
			onMenuItemSelectedListener.onReposSelected();
			onMenuItemSelectedListener.onMenuItemSelected(currentSelectedItem, true);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onMenuItemSelected(MenuItem item) {
		if (item != null && onMenuItemSelectedListener != null) {
			boolean changeTitle = true;
			switch (item.parentId) {
				case 1:
					changeTitle = itemUser(item);
					break;
				case 2:
					changeTitle = itemRepositories(item);
					break;
				case 3:
					changeTitle = itemPeople(item);
					break;
				case 4:
					changeTitle = itemExtras(item);
					break;
			}
			onMenuItemSelectedListener.onMenuItemSelected(item, changeTitle);
		}
	}

	private boolean itemUser(MenuItem item) {
		boolean change = true;
		switch (item.id) {
			case 1:
				change = onMenuItemSelectedListener.onUserEventsSelected();
				break;
		}
		return change;
	}

	private boolean itemRepositories(MenuItem item) {
		boolean change = true;
		switch (item.id) {
			case 0:
				change = onMenuItemSelectedListener.onReposSelected();
				break;
			case 1:
				change = onMenuItemSelectedListener.onStarredSelected();
				break;
			case 2:
				change = onMenuItemSelectedListener.onWatchedSelected();
				break;
		}
		return change;
	}

	private boolean itemPeople(MenuItem item) {
		boolean change = true;
		switch (item.id) {
			case 0:
				change = onMenuItemSelectedListener.onPeopleSelected();
				break;
		}
		return change;
	}

	private boolean itemExtras(MenuItem item) {
		boolean change = true;
		switch (item.id) {
			case 0:
				change = onMenuItemSelectedListener.onSettingsSelected();
				break;
			case 1:
				change = onMenuItemSelectedListener.onAboutSelected();
				break;
		}
		return change;
	}

	public void setOnMenuItemSelectedListener(OnMenuItemSelectedListener onMenuItemSelectedListener) {
		this.onMenuItemSelectedListener = onMenuItemSelectedListener;
	}

	@Override
	public void onResponseOk(User user, Response r) {
		ImageLoader.getInstance().displayImage(user.avatar_url, userAvatar);
		userLogin.setText(user.login);
		userName.setText(user.name);
	}

	@Override
	public void onFail(RetrofitError error) {

	}

	@Override
	public void onClick(View v) {
		if (onMenuItemSelectedListener != null) {
			onMenuItemSelectedListener.onProfileSelected();
			onMenuItemSelectedListener.closeMenu();
		}
	}

	public interface OnMenuItemSelectedListener {
		boolean onProfileSelected();

		boolean onReposSelected();

		boolean onStarredSelected();

		boolean onWatchedSelected();

		boolean onPeopleSelected();

		void onMenuItemSelected(@NonNull MenuItem item, boolean changeTitle);

		void closeMenu();

		boolean onUserEventsSelected();

		boolean onSettingsSelected();

		boolean onAboutSelected();
	}
}

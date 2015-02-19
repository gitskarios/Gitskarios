package com.alorma.github.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.Notification;
import com.alorma.github.sdk.services.notifications.GetNotificationsClient;
import com.alorma.github.ui.adapter.NotificationsAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.alorma.github.ui.view.DirectionalScrollListener;
import com.alorma.githubicons.GithubIconify;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by Bernat on 19/02/2015.
 */
public class NotificationsFragment extends PaginatedListFragment<List<Notification>> {

	private StickyListHeadersListView listView;

	public static NotificationsFragment newInstance() {
		return new NotificationsFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.list_fragment_headers
				, null);
	}

	@Override
	protected void setupListView(View view) {
		listView = (StickyListHeadersListView) view.findViewById(android.R.id.list);
		if (listView != null) {
			listView.setDivider(getResources().getDrawable(R.drawable.divider_main));
			listView.setOnScrollListener(new DirectionalScrollListener(this, this, FAB_ANIM_DURATION));
			listView.setOnItemClickListener(this);
			listView.setAreHeadersSticky(false);
		}
	}

	@Override
	protected void executeRequest() {
		super.executeRequest();

		GetNotificationsClient client = new GetNotificationsClient(getActivity());
		client.setOnResultCallback(this);
		client.execute();
	}

	@Override
	protected void executePaginatedRequest(int page) {

	}

	@Override
	protected void onResponse(final List<Notification> notifications, boolean refreshing) {
		if (notifications != null) {
			if (notifications.size() > 0) {
				Map<String, Integer> ids = new HashMap<>();

				int id = 0;
				for (Notification notification : notifications) {
					if (ids.get(notification.repository.name) == null) {
						ids.put(notification.repository.name, id++);
					}
					notification.adapter_repo_parent_id = ids.get(notification.repository.name);
				}

				Collections.sort(notifications, Notification.Comparators.REPO_ID);

				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						NotificationsAdapter notificationsAdapter = new NotificationsAdapter(getActivity(), notifications);
						listView.setAdapter(notificationsAdapter);
					}
				});

			} else {
				setEmpty();
			}
		}
	}

	@Override
	protected void loadArguments() {

	}

	@Override
	protected GithubIconify.IconValue getNoDataIcon() {
		return GithubIconify.IconValue.octicon_inbox;
	}

	@Override
	protected int getNoDataText() {
		return R.string.no_notifications;
	}
}

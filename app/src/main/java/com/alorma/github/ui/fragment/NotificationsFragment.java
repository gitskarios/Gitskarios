package com.alorma.github.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.Notification;
import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.events.EventType;
import com.alorma.github.sdk.bean.dto.response.events.payload.IssueCommentEventPayload;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.notifications.GetNotificationsClient;
import com.alorma.github.ui.activity.IssueDetailActivity;
import com.alorma.github.ui.activity.RepoDetailActivity;
import com.alorma.github.ui.adapter.NotificationsAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.alorma.github.ui.view.DirectionalScrollListener;
import com.alorma.githubicons.GithubIconify;
import com.google.gson.Gson;

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
	public NotificationsAdapter notificationsAdapter;

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
			listView.setOnItemClickListener(this);
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
						notificationsAdapter = new NotificationsAdapter(getActivity(), notifications);
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

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Notification item = notificationsAdapter.getItem(position);
		String type = item.subject.type;
		
		// https://api.github.com/repos/github/android/issues/666
		Uri uri = Uri.parse(item.subject.url);
		if (type.equalsIgnoreCase("Issue")) {
			List<String> segments = uri.getPathSegments();
			String user = segments.get(1);
			String repo = segments.get(2);
			String number = segments.get(4);
			IssueInfo issueInfo = new IssueInfo();
			issueInfo.num = Integer.valueOf(number);
			issueInfo.repo = new RepoInfo();
			issueInfo.repo.owner = user;
			issueInfo.repo.name = repo;
			Intent launcherIntent = IssueDetailActivity.createLauncherIntent(getActivity(), issueInfo, item.repository.permissions);
			startActivity(launcherIntent);
		} else {
			String fullName = item.repository.full_name;
			String[] parts = fullName.split("/");
			Intent intent = RepoDetailActivity.createLauncherIntent(getActivity(), parts[0], parts[1]);
			startActivity(intent);
		}
	}
}

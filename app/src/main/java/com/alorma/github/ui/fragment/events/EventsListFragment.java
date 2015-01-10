package com.alorma.github.ui.fragment.events;

import android.os.Bundle;
import android.view.View;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.ListEvents;
import com.alorma.github.sdk.services.user.events.GetUserEventsClient;
import com.alorma.github.ui.adapter.events.EventAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.alorma.githubicons.GithubIconify;

/**
 * Created by Bernat on 03/10/2014.
 */
public class EventsListFragment extends PaginatedListFragment<ListEvents>{

	private EventAdapter eventsAdapter;
	private String username;

	public static EventsListFragment newInstance(String username) {
		Bundle bundle = new Bundle();
		bundle.putString(USERNAME, username);

		EventsListFragment f = new EventsListFragment();
		f.setArguments(bundle);

		return f;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (getListView() != null) {
			getListView().setDivider(null);
		}
	}

	@Override
	protected void onResponse(ListEvents githubEvents, boolean refreshing) {
		getListView().setDivider(null);
		if (githubEvents != null && githubEvents.size() > 0) {

			if (eventsAdapter == null || refreshing) {
				eventsAdapter = new EventAdapter(getActivity(), githubEvents);
				setListAdapter(eventsAdapter);
			}

			if (eventsAdapter.isLazyLoading()) {
				if (eventsAdapter != null) {
					eventsAdapter.setLazyLoading(false);
					eventsAdapter.addAll(githubEvents);
				}
			}
			
			if (eventsAdapter != null) {
				setListAdapter(eventsAdapter);
			}
		}
	}

	@Override
	protected void loadArguments() {
		username = getArguments().getString(USERNAME);
	}

	@Override
	protected void executeRequest() {
		super.executeRequest();
		GetUserEventsClient eventsClient = new GetUserEventsClient(getActivity(), username);
		eventsClient.setOnResultCallback(this);
		eventsClient.execute();
	}

	@Override
	protected void executePaginatedRequest(int page) {
		super.executePaginatedRequest(page);

		eventsAdapter.setLazyLoading(true);

		GetUserEventsClient eventsClient = new GetUserEventsClient(getActivity(), username, page);
		eventsClient.setOnResultCallback(this);
		eventsClient.execute();
	}

	@Override
	protected GithubIconify.IconValue getNoDataIcon() {
		return GithubIconify.IconValue.octicon_calendar;
	}

	@Override
	protected int getNoDataText() {
		return R.string.noevents;
	}
}

package com.alorma.github.ui.fragment.issues;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.ListIssues;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.services.pullrequest.GetPullRequestsClient;
import com.alorma.github.ui.activity.PullRequestDetailActivity;
import com.alorma.github.ui.adapter.issues.IssuesAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.alorma.github.ui.listeners.RefreshListener;
import com.alorma.github.ui.listeners.TitleProvider;
import com.alorma.githubicons.GithubIconify;

/**
 * Created by Bernat on 22/08/2014.
 */
public class PullRequestsListFragment extends PaginatedListFragment<ListIssues> implements View.OnClickListener, TitleProvider {

	private String owner;
	private String repository;
	private IssuesAdapter pullRequestsAdapter;

	public static PullRequestsListFragment newInstance(String owner, String repo, RefreshListener listener) {
		Bundle bundle = new Bundle();
		bundle.putString("OWNER", owner);
		bundle.putString("REPO", repo);

		PullRequestsListFragment fragment = new PullRequestsListFragment();
		fragment.setRefreshListener(listener);
		fragment.setArguments(bundle);
		return fragment;
	}

	protected void executeRequest() {
		super.executeRequest();
		if (owner != null && repository != null) {
			GetPullRequestsClient pullRequestsClient = new GetPullRequestsClient(getActivity(), owner, repository);
			pullRequestsClient.setOnResultCallback(this);
			pullRequestsClient.execute();
		}
	}

	@Override
	protected void executePaginatedRequest(int page) {
		super.executePaginatedRequest(page);

		if (pullRequestsAdapter != null) {
			pullRequestsAdapter.setLazyLoading(true);
		}

		if (owner != null && repository != null) {

			GetPullRequestsClient pullRequestsClient = new GetPullRequestsClient(getActivity(), owner, repository, page);
			pullRequestsClient.setOnResultCallback(this);
			pullRequestsClient.execute();
		}
	}

	@Override
	protected void onResponse(ListIssues issues, boolean refreshing) {
		if (issues != null && issues.size() > 0) {

			if (pullRequestsAdapter == null || refreshing) {
				pullRequestsAdapter = new IssuesAdapter(getActivity(), issues);
				setListAdapter(pullRequestsAdapter);
			}

			if (pullRequestsAdapter.isLazyLoading()) {
				if (pullRequestsAdapter != null) {
					pullRequestsAdapter.setLazyLoading(false);
					pullRequestsAdapter.addAll(issues);
				}
			}
		}
	}

	@Override
	protected GithubIconify.IconValue getNoDataIcon() {
		return GithubIconify.IconValue.octicon_git_pull_request;
	}

	@Override
	protected int getNoDataText() {
		return R.string.no_prs_found;
	}

	@Override
	protected void loadArguments() {
		if (getArguments() != null) {
			this.owner = getArguments().getString("OWNER");
			this.repository = getArguments().getString("REPO");
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Issue item = pullRequestsAdapter.getItem(position);
		if (item != null) {
			IssueInfo info = new IssueInfo();
			info.owner = owner;
			info.repo = repository;
			info.num = item.number;

			Intent intent = PullRequestDetailActivity.createLauncherIntent(getActivity(), info);
			startActivity(intent);
		}
	}

	@Override
	public CharSequence getTitle() {
		return getString(R.string.pull_requests_fragment_title);
	}
}

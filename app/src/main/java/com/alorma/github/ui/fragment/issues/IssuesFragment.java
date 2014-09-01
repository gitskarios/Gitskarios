package com.alorma.github.ui.fragment.issues;

import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.ListIssues;
import com.alorma.github.sdk.bean.dto.response.Permissions;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.services.issues.GetIssuesClient;
import com.alorma.github.ui.activity.IssueDetailActivity;
import com.alorma.github.ui.activity.NewIssueActivity;
import com.alorma.github.ui.activity.PullRequestDetailActivity;
import com.alorma.github.ui.adapter.issues.IssuesAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.alorma.github.ui.listeners.RefreshListener;
import com.joanzapata.android.iconify.Iconify;

import fr.dvilleneuve.android.TextDrawable;

/**
 * Created by Bernat on 22/08/2014.
 */
public class IssuesFragment extends PaginatedListFragment<ListIssues> implements View.OnClickListener {

	private static final int ISSUE_REQUEST = 1234;
	private String owner;
	private String repository;
	private float fabNewY;
	private float fabOldY;
	private IssuesAdapter issuesAdapter;
	private Permissions permissions;

	public static IssuesFragment newInstance(String owner, String repo, RefreshListener listener) {
		Bundle bundle = new Bundle();
		bundle.putString("OWNER", owner);
		bundle.putString("REPO", repo);

		IssuesFragment fragment = new IssuesFragment();
		fragment.setRefreshListener(listener);
		fragment.setArguments(bundle);
		return fragment;
	}

	protected void executeRequest() {
		super.executeRequest();
		if (owner != null && repository != null) {
			GetIssuesClient issuesClient = new GetIssuesClient(getActivity(), owner, repository, 0);
			issuesClient.setOnResultCallback(this);
			issuesClient.execute();
		}
	}

	@Override
	protected void executePaginatedRequest(int page) {
		super.executePaginatedRequest(page);

		if (issuesAdapter != null) {
			issuesAdapter.setLazyLoading(true);
		}

		if (owner != null && repository != null) {
			GetIssuesClient issuesClient = new GetIssuesClient(getActivity(), owner, repository, 0, page);
			issuesClient.setOnResultCallback(this);
			issuesClient.execute();
		}
	}

	@Override
	protected void onResponse(ListIssues issues, boolean refreshing) {
		if (issues != null && issues.size() > 0) {

			if (issuesAdapter == null || refreshing) {
				issuesAdapter = new IssuesAdapter(getActivity(), issues);
				setListAdapter(issuesAdapter);
			}

			if (issuesAdapter.isLazyLoading()) {
				if (issuesAdapter != null) {
					issuesAdapter.setLazyLoading(false);
					issuesAdapter.addAll(issues);
				}
			}
		}
	}


	@Override
	protected Iconify.IconValue getNoDataIcon() {
		return Iconify.IconValue.fa_info_circle;
	}

	@Override
	protected int getNoDataText() {
		return R.string.no_issues_found;
	}

	@Override
	protected void loadArguments() {
		if (getArguments() != null) {
			this.owner = getArguments().getString("OWNER");
			this.repository = getArguments().getString("REPO");
		}
	}

	@Override
	protected boolean useInnerSwipeRefresh() {
		return false;
	}

	@Override
	protected boolean useFAB() {
		return permissions == null || permissions.pull;
	}

	@Override
	protected Drawable fabDrawable() {
		TextDrawable drawable = new TextDrawable(getActivity(), "+");
		drawable.color(Color.WHITE);
		drawable.sizeDp(30);
		return drawable;
	}

	@Override
	protected PropertyValuesHolder showAnimator() {
		return PropertyValuesHolder.ofFloat(View.Y, fabNewY, fabOldY);
	}

	@Override
	protected PropertyValuesHolder hideAnimator() {
		fabOldY = fab.getY();
		fabNewY = fab.getY() + fab.getHeight() + (getResources().getDimension(R.dimen.gapLarge) * 2);
		return PropertyValuesHolder.ofFloat(View.Y, fab.getY(), fabNewY);
	}

	@Override
	protected void fabClick() {
		super.fabClick();

		Intent intent = NewIssueActivity.createLauncherIntent(getActivity(), owner, repository, permissions.push);
		startActivityForResult(intent, ISSUE_REQUEST);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_FIRST_USER) {
			invalidate();
		} else if (requestCode == ISSUE_REQUEST && resultCode == Activity.RESULT_OK) {
			invalidate();
		}
	}

	public void invalidate() {
		onRefresh();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Issue item = issuesAdapter.getItem(position);
		if (item != null) {
			item.repository = new Repo();
			item.repository.owner = new User();
			item.repository.owner.login = owner;
			item.repository.name = repository;
			item.repository.permissions = permissions;

			Intent intent = IssueDetailActivity.createLauncherIntent(getActivity(), item);
			if (item.pullRequest != null) {
				//intent = PullRequestDetailActivity.createLauncherIntent(getActivity(), item);
			}
			startActivity(intent);
		}
	}

	public void setPermissions(Permissions permissions) {
		this.permissions = permissions;
	}


}

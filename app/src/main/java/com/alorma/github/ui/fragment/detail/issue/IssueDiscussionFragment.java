package com.alorma.github.ui.fragment.detail.issue;

import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.ListIssueComments;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.services.issues.GetIssueComments;
import com.alorma.github.ui.adapter.detail.issue.IssuesCommentsAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.alorma.githubicons.GithubIconify;

/**
 * Created by Bernat on 23/08/2014.
 */
public class IssueDiscussionFragment extends PaginatedListFragment<ListIssueComments> {

	private static final String ISSUE_INFO = "ISSUE_INFO";
	private IssuesCommentsAdapter adapter;
	private IssueInfo issueInfo;

	public static IssueDiscussionFragment newInstance(IssueInfo info) {
		Bundle bundle = new Bundle();

		bundle.putParcelable(ISSUE_INFO, info);

		IssueDiscussionFragment fragment = new IssueDiscussionFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View v = inflater.inflate(R.layout.issues_discussion_list_fragment, null, false);

		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (getListView() != null) {
			getListView().setDivider(null);
			int int16 = getResources().getDimensionPixelOffset(R.dimen.gapLarge);
			getListView().setPadding(0, int16, 0, 0);
			getListView().setClipToPadding(false);
		}
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

	}

	@Override
	protected void loadArguments() {
		if (getArguments() != null) {
			issueInfo = getArguments().getParcelable(ISSUE_INFO);
		}
	}

	@Override
	protected void executeRequest() {
		super.executeRequest();

		if (issueInfo.repo.owner != null && issueInfo.repo.name != null && issueInfo.num > 0) {
			GetIssueComments issueComments = new GetIssueComments(getActivity(), issueInfo);
			issueComments.setOnResultCallback(this);
			issueComments.execute();
		}
	}

	@Override
	protected void executePaginatedRequest(int page) {
		super.executePaginatedRequest(page);

		adapter.setLazyLoading(true);

		if (issueInfo.repo.owner != null && issueInfo.repo.name != null && issueInfo.num > 0) {
			GetIssueComments issueComments = new GetIssueComments(getActivity(), issueInfo, page);
			issueComments.setOnResultCallback(this);
			issueComments.execute();
		}
	}

	@Override
	protected void onResponse(ListIssueComments issueComments, boolean refreshing) {
		if (issueComments != null && issueComments.size() > 0) {
			if (adapter == null || refreshing) {
				ListIssueComments comments = new ListIssueComments();
				comments.addAll(issueComments);
				adapter = new IssuesCommentsAdapter(getActivity(), comments);
				setListAdapter(adapter);
			} else if (adapter.isLazyLoading()) {
				adapter.setLazyLoading(false);
				adapter.addAll(issueComments);
			}
		}
	}

	@Override
	protected GithubIconify.IconValue getNoDataIcon() {
		return GithubIconify.IconValue.octicon_comment_discussion;
	}

	@Override
	protected int getNoDataText() {
		return R.string.no_comments;
	}

	@Override
	protected PropertyValuesHolder hideAnimator(View fab) {
		float fabNewY = fab.getY() + fab.getHeight() + (getResources().getDimension(R.dimen.gapLarge) * 2);
		return PropertyValuesHolder.ofFloat(View.Y, fab.getY(), fabNewY);
	}
}

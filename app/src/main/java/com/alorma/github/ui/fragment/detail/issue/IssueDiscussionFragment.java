package com.alorma.github.ui.fragment.detail.issue;

import android.animation.PropertyValuesHolder;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.ListIssueComments;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.services.issues.GetIssueComments;
import com.alorma.github.ui.adapter.detail.issue.IssuesCommentsAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.alorma.github.ui.view.FABCenterLayout;
import com.alorma.githubicons.GithubIconDrawable;
import com.alorma.githubicons.GithubIconify;

/**
 * Created by Bernat on 23/08/2014.
 */
public class IssueDiscussionFragment extends PaginatedListFragment<ListIssueComments> {

	private static final String ISSUE_INFO = "ISSUE_INFO";
	private IssuesCommentsAdapter adapter;
	private float fabOldY;
	private float fabNewY;
	private IssueDiscussionListener issueDiscussionListener;
	private IssueInfo issueInfo;
	private FABCenterLayout fabLayout;

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

		fabLayout = (FABCenterLayout) view.findViewById(R.id.fabLayout);
		fabLayout.setFabColor(getResources().getColor(R.color.accent));
		fabLayout.setFabColorPressed(getResources().getColor(R.color.primary_dark));
		GithubIconDrawable drawable = new GithubIconDrawable(getActivity(), getFABGithubIcon()).color(Color.WHITE).fabSize();
		fabLayout.setFabIcon(drawable);
		fabLayout.setFabClickListener(this, getString(R.string.add_comment));

		if (issueDiscussionListener != null) {
			Issue issue = issueDiscussionListener.requestIssue();

			TextView issueBody = (TextView) view.findViewById(R.id.issueBody);
			issueBody.setText(issue.body);
		}

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
		MenuItem menuItem = menu.add(0, R.id.action_fold_issue, 0, R.string.fold_issue);
		menuItem.setIcon(new GithubIconDrawable(getActivity(), GithubIconify.IconValue.octicon_fold).actionBarSize().colorRes(R.color.white));
		menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
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

		if (issueInfo.owner != null && issueInfo.repo != null && issueInfo.num > 0) {
			GetIssueComments issueComments = new GetIssueComments(getActivity(), issueInfo.owner, issueInfo.repo, issueInfo.num);
			issueComments.setOnResultCallback(this);
			issueComments.execute();
		}
	}

	@Override
	protected void executePaginatedRequest(int page) {
		super.executePaginatedRequest(page);

		adapter.setLazyLoading(true);

		if (issueInfo.owner != null && issueInfo.repo != null && issueInfo.num > 0) {
			GetIssueComments issueComments = new GetIssueComments(getActivity(), issueInfo.owner, issueInfo.repo, issueInfo.num, page);
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
	protected boolean useInnerSwipeRefresh() {
		return true;
	}

	@Override
	protected GithubIconify.IconValue getFABGithubIcon() {
		return GithubIconify.IconValue.octicon_comment_discussion;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == fabLayout.getFabId()) {
			if (issueDiscussionListener != null) {
				issueDiscussionListener.onAddComment();
			}
		} else {
			super.onClick(v);
		}
	}

	@Override
	protected PropertyValuesHolder hideAnimator(View fab) {
		fabOldY = fab.getY();
		fabNewY = fab.getY() + fab.getHeight() + (getResources().getDimension(R.dimen.gapLarge) * 2);
		return PropertyValuesHolder.ofFloat(View.Y, fab.getY(), fabNewY);
	}

	public void setIssueDiscussionListener(IssueDiscussionListener issueDiscussionListener) {
		this.issueDiscussionListener = issueDiscussionListener;
	}

	public interface IssueDiscussionListener {
		Issue requestIssue();

		void onAddComment();
	}
}

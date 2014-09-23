package com.alorma.github.ui.fragment.detail.issue;

import android.animation.PropertyValuesHolder;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.sdk.bean.dto.response.IssueComment;
import com.alorma.github.sdk.bean.dto.response.ListEvents;
import com.alorma.github.sdk.bean.dto.response.ListIssueComments;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.issues.GetIssueComments;
import com.alorma.github.ui.adapter.detail.issue.IssuesCommentsAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.joanzapata.android.iconify.Iconify;

import java.util.ArrayList;
import java.util.List;

import fr.dvilleneuve.android.TextDrawable;
import retrofit.RetrofitError;
import retrofit.client.Response;

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

	public static IssueDiscussionFragment newInstance(IssueInfo info) {
		Bundle bundle = new Bundle();

		bundle.putParcelable(ISSUE_INFO, info);

		IssueDiscussionFragment fragment = new IssueDiscussionFragment();
		fragment.setArguments(bundle);
		return fragment;
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
				if (issueDiscussionListener != null) {
					comments.add(issueDiscussionListener.requestIssue());
				}
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
	protected Iconify.IconValue getNoDataIcon() {
		return Iconify.IconValue.fa_comment;
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
	protected boolean useFAB() {
		return false;
	}

	@Override
	protected void fabClick() {

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

	public void setIssueDiscussionListener(IssueDiscussionListener issueDiscussionListener) {
		this.issueDiscussionListener = issueDiscussionListener;
	}

	public interface IssueDiscussionListener {
		IssueComment requestIssue();
	}
}

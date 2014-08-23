package com.alorma.github.ui.fragment.detail.issue;

import android.animation.PropertyValuesHolder;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.ListIssueComments;
import com.alorma.github.sdk.services.issues.comments.GetIssueComments;
import com.alorma.github.ui.adapter.detail.issue.IssuesCommentsAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.joanzapata.android.iconify.Iconify;

import fr.dvilleneuve.android.TextDrawable;

/**
 * Created by Bernat on 23/08/2014.
 */
public class IssueDiscussionFragment extends PaginatedListFragment<ListIssueComments>{

    private String owner;
    private String repository;
    private int num;
    private IssuesCommentsAdapter adapter;
    private float fabOldY;
    private float fabNewY;

    public static IssueDiscussionFragment newInstance(String owner, String repo, int num) {
        Bundle bundle = new Bundle();
        bundle.putString("OWNER", owner);
        bundle.putString("REPO", repo);
        bundle.putInt("NUM", num);

        IssueDiscussionFragment fragment = new IssueDiscussionFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void loadArguments() {
        if (getArguments() != null) {
            this.owner = getArguments().getString("OWNER");
            this.repository = getArguments().getString("REPO");
            this.num = getArguments().getInt("NUM");
        }
    }

    @Override
    protected void executeRequest() {
        super.executeRequest();

        if (owner != null && repository != null && num > 0) {
            GetIssueComments issueComments = new GetIssueComments(getActivity(), owner, repository, num);
            issueComments.setOnResultCallback(this);
            issueComments.execute();
        }
    }

    @Override
    protected void executePaginatedRequest(int page) {
        super.executePaginatedRequest(page);

        adapter.setLazyLoading(true);

        if (owner != null && repository != null && num > 0) {
            GetIssueComments issueComments = new GetIssueComments(getActivity(), owner, repository, num, page);
            issueComments.setOnResultCallback(this);
            issueComments.execute();
        }
    }

    @Override
    protected void onResponse(ListIssueComments issueComments, boolean refreshing) {
        if (issueComments != null && issueComments.size() > 0) {
            if (adapter == null || refreshing) {
                adapter = new IssuesCommentsAdapter(getActivity(), issueComments);
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
        return false;
    }

    @Override
    protected boolean useFAB() {
        return true;
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
    @Override
    protected Drawable fabDrawable() {
        TextDrawable drawable = new TextDrawable(getActivity(), "+");
        drawable.color(Color.WHITE);
        drawable.sizeDp(30);
        return drawable;
    }
}

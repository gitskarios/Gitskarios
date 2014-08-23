package com.alorma.github.ui.adapter.detail.issue;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.alorma.github.sdk.bean.dto.response.ListIssueComments;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import fr.dvilleneuve.android.TextDrawable;

/**
 * Created by Bernat on 23/08/2014.
 */
public class IssueDiscussionFragment extends PaginatedListFragment<ListIssueComments>{

    private String owner;
    private String repository;
    private int num;

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
    protected void onResponse(ListIssueComments issueComments, boolean refreshing) {

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
    protected Iconify.IconValue getNoDataIcon() {
        return null;
    }

    @Override
    protected int getNoDataText() {
        return 0;
    }

    @Override
    protected boolean useInnerSwipeRefresh() {
        return false;
    }

    @Override
    protected boolean useFAB() {
        return super.useFAB();
    }

    @Override
    protected void fabClick() {

    }

    @Override
    protected Drawable fabDrawable() {
        TextDrawable drawable = new TextDrawable(getActivity(), "+");
        drawable.color(Color.WHITE);
        return drawable;
    }
}

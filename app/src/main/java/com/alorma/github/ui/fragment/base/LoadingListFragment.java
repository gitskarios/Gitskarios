package com.alorma.github.ui.fragment.base;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.ui.listeners.RefreshListener;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

/**
 * Created by Bernat on 05/08/2014.
 */
public abstract class LoadingListFragment extends BaseListFragment implements SwipeRefreshLayout.OnRefreshListener {

	private SwipeRefreshLayout swipe;
	private RefreshListener refreshListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View v = inflater.inflate(R.layout.list_fragment, null, false);

		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe);

		if (useInnerSwipeRefresh()) {
			swipe.setColorSchemeResources(R.color.accent,
					R.color.gray_github_light,
					R.color.accentDark,
					R.color.gray_github_light);
		} else {
			swipe.setColorSchemeResources(android.R.color.transparent,
					android.R.color.transparent,
					android.R.color.transparent,
					android.R.color.transparent);
		}
		swipe.setOnRefreshListener(this);
	}

	protected boolean useInnerSwipeRefresh() {
		return true;
	}

	public void setRefreshListener(RefreshListener refreshListener) {
		this.refreshListener = refreshListener;
	}

	protected void startRefresh() {
		if (useInnerSwipeRefresh() && swipe != null) {
			swipe.setRefreshing(true);
		} else if (refreshListener != null) {
			refreshListener.showRefresh();
		}
	}

	protected void stopRefresh() {
		if (useInnerSwipeRefresh() && swipe != null) {
			swipe.setRefreshing(false);
		} else if (refreshListener != null) {
			refreshListener.cancelRefresh();
		}
	}
}
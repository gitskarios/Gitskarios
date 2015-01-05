package com.alorma.github.ui.fragment.base;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alorma.github.R;
import com.alorma.github.ui.listeners.RefreshListener;
import com.alorma.github.utils.AttributesUtils;

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

		int accent = AttributesUtils.getAttributeId(getActivity(), R.style.AppTheme_Repos, R.attr.colorAccent);
		int primaryDark = AttributesUtils.getAttributeId(getActivity(), R.style.AppTheme_Repos, R.attr.colorPrimaryDark);

		if (swipe != null) {
			swipe.setColorSchemeResources(accent,
					R.color.gray_github_light,
					primaryDark,
					R.color.gray_github_light);
			swipe.setOnRefreshListener(this);
		}
	}

	public void setRefreshListener(RefreshListener refreshListener) {
		this.refreshListener = refreshListener;
	}

	protected void startRefresh() {
		if (swipe != null) {
			swipe.setRefreshing(true);
		} else if (refreshListener != null) {
			refreshListener.showRefresh();
		}
	}

	protected void stopRefresh() {
		if (swipe != null) {
			swipe.setRefreshing(false);
		} else if (refreshListener != null) {
			refreshListener.cancelRefresh();
		}
	}
}
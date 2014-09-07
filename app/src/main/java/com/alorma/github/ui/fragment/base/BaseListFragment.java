package com.alorma.github.ui.fragment.base;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.ListFragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import com.alorma.github.ui.view.DirectionalScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alorma.github.R;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

/**
 * Created by Bernat on 12/08/2014.
 */
public abstract class BaseListFragment extends ListFragment implements AbsListView.OnScrollListener, DirectionalScrollListener.OnDetectScrollListener, DirectionalScrollListener.OnCancelableDetectScrollListener, View.OnClickListener {

	private static final long FAB_ANIM_DURATION = 400;
	protected TextView emptyText;
	protected ImageView emptyIcon;
	protected View emptyLy;
	protected ImageView fab;
	private ValueAnimator animator;
	private boolean fabVisible;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.base_list, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		getListView().setOnScrollListener(new DirectionalScrollListener(this, this, FAB_ANIM_DURATION));

		getListView().setDivider(null);
		getListView().setDividerHeight(0);

		emptyIcon = (ImageView) view.findViewById(R.id.emptyIcon);
		emptyText = (TextView) view.findViewById(R.id.emptyText);
		emptyLy = view.findViewById(R.id.emptyLayout);

		ListView listView = getListView();

		if (listView != null) {
			listView.setDivider(getResources().getDrawable(R.drawable.divider_main));
		}

		fab = (ImageView) view.findViewById(R.id.fabButton);

		if (fab != null) {
			if (useFAB() && fabDrawable() != null) {
				fabVisible = true;
				fab.setImageDrawable(fabDrawable());
				fab.setOnClickListener(this);
			} else {
				fab.setVisibility(View.GONE);
			}
		}

		loadArguments();
	}

	protected Drawable fabDrawable() {
		return null;
	}

	protected abstract void loadArguments();

	protected boolean useFAB() {
		return false;
	}

	public void setEmpty() {
		if (getActivity() != null) {
			if (emptyText != null && emptyIcon != null) {
				if (getNoDataIcon() != null && getNoDataText() > 0) {
					IconDrawable iconDrawable = new IconDrawable(getActivity(), getNoDataIcon());
					iconDrawable.colorRes(R.color.gray_github_medium);
					emptyIcon.setImageDrawable(iconDrawable);

					emptyText.setText(getNoDataText());

					emptyLy.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	protected abstract Iconify.IconValue getNoDataIcon();

	protected abstract int getNoDataText();

	private void showFab() {
		if (!fabVisible) {
			fabVisible = true;
			PropertyValuesHolder pvh = showAnimator();
			startAnimator(pvh);
		}
	}

	private void hideFab() {
		if (fabVisible & (animator == null || !animator.isRunning())) {
			fabVisible = false;
			PropertyValuesHolder pvh = hideAnimator();
			startAnimator(pvh);
		}
	}

	private void startAnimator(PropertyValuesHolder pvh) {
		if (pvh != null) {
			animator = ObjectAnimator.ofPropertyValuesHolder(fab, pvh);
			animator.setDuration(FAB_ANIM_DURATION);
			animator.setRepeatCount(0);
			animator.start();
		}
	}

	protected PropertyValuesHolder showAnimator() {
		PropertyValuesHolder pvh = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f);
		return pvh;
	}

	protected PropertyValuesHolder hideAnimator() {
		PropertyValuesHolder pvh = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0f);
		return pvh;
	}

	@Override
	public void onUpScrolling() {
		hideFab();
	}

	@Override
	public void onDownScrolling() {
		hideFab();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

	}

	@Override
	public void onScrollStop() {
		showFab();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.fabButton) {
			fabClick();
		}
	}

	protected void fabClick() {

	}
}

package com.alorma.github.ui.fragment.base;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.ui.view.DirectionalScrollListener;
import com.alorma.githubicons.GithubIconDrawable;
import com.alorma.githubicons.GithubIconify;
import com.getbase.floatingactionbutton.FloatingActionButton;

/**
 * Created by Bernat on 12/08/2014.
 */
public abstract class BaseListFragment extends Fragment implements AbsListView.OnScrollListener, DirectionalScrollListener.OnDetectScrollListener, DirectionalScrollListener.OnCancelableDetectScrollListener, View.OnClickListener, AdapterView.OnItemClickListener {

	protected static final long FAB_ANIM_DURATION = 400;
	protected TextView emptyText;
	protected ImageView emptyIcon;
	protected View emptyLy;
	private FloatingActionButton fab;
	private ValueAnimator animator;
	private boolean fabVisible;
	private ListView listView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.base_list, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		setupListView(view);


		emptyIcon = (ImageView) view.findViewById(R.id.emptyIcon);
		emptyText = (TextView) view.findViewById(R.id.emptyText);
		emptyLy = view.findViewById(R.id.emptyLayout);


		fab = (FloatingActionButton) view.findViewById(R.id.fabButton);

		checkFAB();

		loadArguments();
	}

	protected void setupListView(View view) {
		listView = (ListView) view.findViewById(android.R.id.list);

		if (listView != null) {
			listView.setOnItemClickListener(this);

			listView.setOnScrollListener(new DirectionalScrollListener(this, this, FAB_ANIM_DURATION));

			listView.setDivider(getResources().getDrawable(R.drawable.divider_main));
		}
	}

	protected void checkFAB() {
		if (getActivity() != null && fab != null) {
			if (useFAB()) {
				fabVisible = true;
				fab.setOnClickListener(this);
				fab.setSize(FloatingActionButton.SIZE_NORMAL);
				GithubIconDrawable drawable = new GithubIconDrawable(getActivity(), getFABGithubIcon()).color(Color.WHITE).fabSize();

				fab.setIconDrawable(drawable);
			} else {
				fab.setVisibility(View.GONE);
			}
		}
	}

	protected abstract void loadArguments();

	protected boolean useFAB() {
		return false;
	}

	public void setEmpty() {
		if (getActivity() != null) {
			if (emptyText != null && emptyIcon != null) {
				if (getNoDataIcon() != null && getNoDataText() > 0) {
					GithubIconDrawable iconDrawable = new GithubIconDrawable(getActivity(), getNoDataIcon());
					iconDrawable.colorRes(R.color.gray_github_medium);
					emptyIcon.setImageDrawable(iconDrawable);

					emptyText.setText(getNoDataText());

					emptyLy.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	protected abstract GithubIconify.IconValue getNoDataIcon();

	protected abstract int getNoDataText();

	private void showFab() {
		if (useFAB() && !fabVisible) {
			fabVisible = true;
			PropertyValuesHolder pvh = showAnimator(fab);
			startAnimator(pvh);
		}
	}

	private void hideFab() {
		if (useFAB() && fabVisible & (animator == null || !animator.isRunning())) {
			fabVisible = false;
			PropertyValuesHolder pvh = hideAnimator(fab);
			startAnimator(pvh);
		}
	}

	private void startAnimator(PropertyValuesHolder pvh) {
		if (useFAB() && pvh != null) {
			animator = ObjectAnimator.ofPropertyValuesHolder(fab, pvh);
			animator.setDuration(FAB_ANIM_DURATION);
			animator.setRepeatCount(0);
			animator.start();
		}
	}

	protected PropertyValuesHolder showAnimator(View fab) {
		PropertyValuesHolder pvh = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f);
		return pvh;
	}

	protected PropertyValuesHolder hideAnimator(View fab) {
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

	protected GithubIconify.IconValue getFABGithubIcon() {
		return GithubIconify.IconValue.octicon_squirrel;
	}

	public ListView getListView() {
		return listView;

	}

	public void setListAdapter(ListAdapter adapter) {
		if (listView != null) {
			listView.setAdapter(adapter);
		}
	}

	public ListAdapter getListAdapter() {
		if (listView != null) {
			return listView.getAdapter();
		} else {
			return null;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		onListItemClick(listView, view, position, id);
	}

	public void onListItemClick(ListView l, View v, int position, long id) {

	}
}

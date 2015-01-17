package com.alorma.github.ui.fragment.repos;

import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.alorma.github.R;
import com.alorma.github.sdk.services.repos.BaseReposClient;
import com.alorma.github.sdk.services.repos.UserReposClient;
import com.alorma.github.ui.activity.NewRepoActivity;

public class ReposFragment extends BaseReposListFragment {

	private static final int NEW_REPO_REQUEST = 8978;
	private String username;
	private float fabOldY;
	private float fabNewY;

	public static ReposFragment newInstance() {
		return new ReposFragment();
	}

	public static ReposFragment newInstance(String username) {
		ReposFragment reposFragment = new ReposFragment();
		if (username != null) {
			Bundle bundle = new Bundle();
			bundle.putString(USERNAME, username);

			reposFragment.setArguments(bundle);
		}
		return reposFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			username = getArguments().getString(USERNAME);
		}
	}

	@Override
	protected void loadArguments() {

	}

	@Override
	protected void executeRequest() {
		super.executeRequest();
		BaseReposClient client = new UserReposClient(getActivity(), username);
		client.setOnResultCallback(this);
		client.execute();
	}

	@Override
	protected void executePaginatedRequest(int page) {
		super.executePaginatedRequest(page);
		UserReposClient client = new UserReposClient(getActivity(), username, page);
		client.setOnResultCallback(this);
		client.execute();
	}

	@Override
	protected int getNoDataText() {
		return R.string.no_repositories;
	}

	@Override
	protected boolean useFAB() {
		return false;
	}

	@Override
	protected void fabClick() {
		super.fabClick();
		Intent intent = new Intent(getActivity(), NewRepoActivity.class);
		startActivityForResult(intent, NEW_REPO_REQUEST);
	}

	@Override
	protected PropertyValuesHolder showAnimator(View fab) {
		return PropertyValuesHolder.ofFloat(View.Y, fabNewY, fabOldY);
	}

	@Override
	protected PropertyValuesHolder hideAnimator(View fab) {
		fabOldY = fab.getY();
		fabNewY = fab.getY() + fab.getHeight() + (getResources().getDimension(R.dimen.gapLarge) * 2);
		return PropertyValuesHolder.ofFloat(View.Y, fab.getY(), fabNewY);
	}

}

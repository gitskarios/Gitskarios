package com.alorma.github.ui.fragment.commit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Branch;
import com.alorma.github.sdk.bean.dto.response.Commit;
import com.alorma.github.sdk.bean.dto.response.ListBranches;
import com.alorma.github.sdk.bean.dto.response.ListCommit;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.commit.ListCommitsClient;
import com.alorma.github.sdk.services.repo.GetRepoBranchesClient;
import com.alorma.github.ui.activity.CommitDetailActivity;
import com.alorma.github.ui.adapter.commit.CommitsAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.alorma.github.ui.listeners.RefreshListener;
import com.alorma.github.ui.listeners.TitleProvider;
import com.alorma.githubicons.GithubIconify;
import com.joanzapata.android.iconify.Iconify;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 07/09/2014.
 */
public class CommitsListFragment extends PaginatedListFragment<ListCommit> implements TitleProvider {
	private static final String OWNER = "OWNER";
	private static final String REPO = "REPO";
	private RepoInfo info;
	private Branch currentBranch;
	private CommitsAdapter commitsAdapter;

	public static CommitsListFragment newInstance(String owner, String repo, RefreshListener listener) {
		Bundle bundle = new Bundle();
		bundle.putString(OWNER, owner);
		bundle.putString(REPO, repo);

		CommitsListFragment fragment = new CommitsListFragment();
		fragment.setRefreshListener(listener);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	protected void onResponse(ListCommit commits, boolean refreshing) {
		if (commits != null && commits.size() > 0) {

			if (commitsAdapter == null || refreshing) {
				commitsAdapter = new CommitsAdapter(getActivity(), commits);
				setListAdapter(commitsAdapter);
			}

			if (commitsAdapter.isLazyLoading()) {
				if (commitsAdapter != null) {
					commitsAdapter.setLazyLoading(false);
					commitsAdapter.addAll(commits);
				}
			}
		}
	}

	@Override
	protected void executeRequest() {
		super.executeRequest();
		ListCommitsClient client = new ListCommitsClient(getActivity(), info, 0, currentBranch);
		client.setOnResultCallback(this);
		client.execute();
	}

	@Override
	protected void executePaginatedRequest(int page) {
		super.executePaginatedRequest(page);
		commitsAdapter.setLazyLoading(true);
		ListCommitsClient client = new ListCommitsClient(getActivity(), info, page, currentBranch);
		client.setOnResultCallback(this);
		client.execute();
	}

	@Override
	protected void loadArguments() {
		info = new RepoInfo();
		if (getArguments() != null) {
			info.owner = getArguments().getString(OWNER);
			info.repo = getArguments().getString(REPO);
		}
	}

	@Override
	protected Iconify.IconValue getNoDataIcon() {
		return Iconify.IconValue.fa_code_fork;
	}

	@Override
	protected int getNoDataText() {
		return R.string.no_commits;
	}

	@Override
	protected boolean useInnerSwipeRefresh() {
		return true;
	}

	@Override
	public CharSequence getTitle() {
		return getString(R.string.commits_fragment_title);
	}

	@Override
	protected boolean useFAB() {
		return true;
	}

	@Override
	protected GithubIconify.IconValue getFABGithubIcon() {
		return GithubIconify.IconValue.octicon_repo_forked;
	}

	@Override
	protected void fabClick() {
		GetRepoBranchesClient repoBranchesClient = new GetRepoBranchesClient(getActivity(), info.owner, info.repo);
		repoBranchesClient.setOnResultCallback(new BranchesCallback());
		repoBranchesClient.execute();
	}

	private class BranchesCallback implements BaseClient.OnResultCallback<ListBranches>, MaterialDialog.ListCallback {

		private ListBranches branches;

		@Override
		public void onResponseOk(ListBranches branches, Response r) {
			this.branches = branches;
			if (branches != null) {
				MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
				String[] names = new String[branches.size()];
				int selectedIndex = 0;
				for (int i = 0; i < branches.size(); i++) {
					String branchName = branches.get(i).name;
					names[i] = branchName;
					if (((currentBranch != null) && branchName.equalsIgnoreCase(currentBranch.toString())) || branchName.equalsIgnoreCase("master")) {
						selectedIndex = i;
					}
				}
				builder.autoDismiss(true);
				builder.items(names);
				builder.itemsCallbackSingleChoice(selectedIndex, this);
				builder.build().show();
			}
		}

		@Override
		public void onFail(RetrofitError error) {

		}

		@Override
		public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
			currentBranch = branches.get(i);
			materialDialog.dismiss();
			commitsAdapter.clear();
			startRefresh();
			refreshing = true;
			executeRequest();
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Commit item = commitsAdapter.getItem(position);

		Intent intent = CommitDetailActivity.launchIntent(getActivity(), info, item.sha);
		startActivity(intent);
	}
}

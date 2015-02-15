package com.alorma.github.ui.fragment.commit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Branch;
import com.alorma.github.sdk.bean.dto.response.Commit;
import com.alorma.github.sdk.bean.dto.response.ListBranches;
import com.alorma.github.sdk.bean.dto.response.ListCommit;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.commit.ListCommitsClient;
import com.alorma.github.sdk.services.content.GetArchiveLinkService;
import com.alorma.github.sdk.services.repo.GetRepoBranchesClient;
import com.alorma.github.sdk.utils.GitskariosSettings;
import com.alorma.github.ui.activity.CommitDetailActivity;
import com.alorma.github.ui.adapter.commit.CommitsAdapter;
import com.alorma.github.ui.callbacks.DialogBranchesCallback;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.alorma.github.ui.listeners.TitleProvider;
import com.alorma.github.ui.view.DirectionalScrollListener;
import com.alorma.githubicons.GithubIconify;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by Bernat on 07/09/2014.
 */
public class CommitsListFragment extends PaginatedListFragment<ListCommit> implements TitleProvider {

	private static final String REPO_INFO = "REPO_INFO";

	private CommitsAdapter commitsAdapter;
	private List<Commit> commitsMap;
	private StickyListHeadersListView listView;
	
	private RepoInfo repoInfo;
	private TimeTickBroadcastReceiver timeTickBroadcastReceiver;

	public static CommitsListFragment newInstance(RepoInfo repoInfo) {
		Bundle bundle = new Bundle();
		bundle.putParcelable(REPO_INFO, repoInfo);

		CommitsListFragment fragment = new CommitsListFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.list_fragment_headers
				, null);
	}


	@Override
	protected void setupListView(View view) {
		listView = (StickyListHeadersListView) view.findViewById(android.R.id.list);
		if (listView != null) {
			listView.setDivider(getResources().getDrawable(R.drawable.divider_main));
			listView.setOnScrollListener(new DirectionalScrollListener(this, this, FAB_ANIM_DURATION));
			listView.setOnItemClickListener(this);
			listView.setAreHeadersSticky(false);
		}
	}

	@Override
	protected void onResponse(ListCommit commits, boolean refreshing) {
		if (commitsMap == null || refreshing) {
			commitsMap = new ArrayList<>();
		}
		if (commits != null && commits.size() > 0) {

			orderCommits(commits);

			if (commitsAdapter == null || refreshing) {
				commitsAdapter = new CommitsAdapter(getActivity(), commitsMap);
				listView.setAdapter(commitsAdapter);
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
	public void onResume() {
		super.onResume();

		IntentFilter filter = new IntentFilter();

		filter.addAction(Intent.ACTION_TIME_TICK);
		filter.addAction(Intent.ACTION_TIME_CHANGED);
		filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

		timeTickBroadcastReceiver = new TimeTickBroadcastReceiver();
		
		getActivity().registerReceiver(timeTickBroadcastReceiver, filter);
	}

	@Override
	public void onPause() {
		getActivity().unregisterReceiver(timeTickBroadcastReceiver);
		super.onPause();
	}

	private class TimeTickBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (commitsAdapter != null) {
						commitsAdapter.notifyDataSetChanged();
					}
				}
			});
		}
	}

	private void orderCommits(ListCommit commits) {

		for (Commit commit : commits) {
			if (commit.commit.author.date != null) {
				DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
				DateTime dt = formatter.parseDateTime(commit.commit.author.date);

				Days days = Days.daysBetween(dt.withTimeAtStartOfDay(), new DateTime(System.currentTimeMillis()).withTimeAtStartOfDay());

				commit.days = days.getDays();
				
				commitsMap.add(commit);
			}
		}
	}

	@Override
	protected void executeRequest() {
		super.executeRequest();
		ListCommitsClient client = new ListCommitsClient(getActivity(), repoInfo, 0);
		client.setOnResultCallback(this);
		client.execute();
	}

	@Override
	protected void executePaginatedRequest(int page) {
		super.executePaginatedRequest(page);
		commitsAdapter.setLazyLoading(true);
		ListCommitsClient client = new ListCommitsClient(getActivity(), repoInfo, page);
		client.setOnResultCallback(this);
		client.execute();
	}

	@Override
	protected void loadArguments() {
		if (getArguments() != null) {
			repoInfo = getArguments().getParcelable(REPO_INFO);
		}
	}

	@Override
	protected GithubIconify.IconValue getNoDataIcon() {
		return GithubIconify.IconValue.octicon_file_diff;
	}

	@Override
	protected int getNoDataText() {
		return R.string.no_commits;
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
		GetRepoBranchesClient repoBranchesClient = new GetRepoBranchesClient(getActivity(), repoInfo);
		repoBranchesClient.setOnResultCallback(new ChangeBranchCallback(getActivity(), repoInfo));
		repoBranchesClient.execute();
	}

	private class ChangeBranchCallback extends DialogBranchesCallback {

		public ChangeBranchCallback(Context context, RepoInfo repoInfo) {
			super(context, repoInfo);
		}

		@Override
		protected void onBranchSelected(String branch) {
			repoInfo.branch = branch;

			if (commitsAdapter != null) {
				commitsAdapter.clear();
			}
			startRefresh();
			refreshing = true;
			executeRequest();
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Commit item = commitsAdapter.getItem(position);

		Intent intent = CommitDetailActivity.launchIntent(getActivity(), repoInfo, item.sha);
		startActivity(intent);
	}
}

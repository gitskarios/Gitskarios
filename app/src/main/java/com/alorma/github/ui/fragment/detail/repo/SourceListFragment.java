package com.alorma.github.ui.fragment.detail.repo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Branch;
import com.alorma.github.sdk.bean.dto.response.Content;
import com.alorma.github.sdk.bean.dto.response.ListContents;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.content.GetArchiveLinkService;
import com.alorma.github.sdk.services.repo.GetRepoBranchesClient;
import com.alorma.github.sdk.services.repo.GetRepoContentsClient;
import com.alorma.github.sdk.utils.GitskariosSettings;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.activity.FileActivity;
import com.alorma.github.ui.adapter.FakeAdapter;
import com.alorma.github.ui.adapter.detail.repo.RepoSourceAdapter;
import com.alorma.github.ui.callbacks.DialogBranchesCallback;
import com.alorma.github.ui.fragment.base.LoadingListFragment;
import com.alorma.github.ui.listeners.TitleProvider;
import com.alorma.githubicons.GithubIconDrawable;
import com.alorma.githubicons.GithubIconify;
import com.crashlytics.android.Crashlytics;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 20/07/2014.
 */
public class SourceListFragment extends LoadingListFragment implements BaseClient.OnResultCallback<ListContents>, TitleProvider {

	private static final String REPO_INFO = "REPO_INFO";

	private RepoInfo repoInfo;

	private RepoSourceAdapter contentAdapter;
	private Map<Content, ListContents> treeContent;
	private Content rootContent = new Content();
	private Content currentSelectedContent = rootContent;
	private FloatingActionButton fabUp;
	private FloatingActionsMenu fabMenu;

	public static SourceListFragment newInstance(RepoInfo repoInfo) {
		Bundle bundle = new Bundle();
		bundle.putParcelable(REPO_INFO, repoInfo);

		SourceListFragment f = new SourceListFragment();
		f.setArguments(bundle);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list_fragment_breadcumbs, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		view.setBackgroundColor(getResources().getColor(R.color.gray_github_light));

		fabMenu = (FloatingActionsMenu) view.findViewById(R.id.fab_menu);

		FloatingActionButton fabDownload = (FloatingActionButton) view.findViewById(R.id.fab_download);
		GithubIconDrawable downloadIcon = new GithubIconDrawable(getActivity(), GithubIconify.IconValue.octicon_cloud_download);
		downloadIcon.colorRes(R.color.white);
		downloadIcon.sizeRes(R.dimen.fab_size_mini_icon);
		fabDownload.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				GetRepoBranchesClient repoBranchesClient = new GetRepoBranchesClient(getActivity(), repoInfo);
				repoBranchesClient.setOnResultCallback(new DownloadBranchesCallback(getActivity(), repoInfo));
				repoBranchesClient.execute();
				fabMenu.collapse();
			}
		});

		fabDownload.setIconDrawable(downloadIcon);

		FloatingActionButton fabBranches = (FloatingActionButton) view.findViewById(R.id.fab_branches);
		GithubIconDrawable branchesIcon = new GithubIconDrawable(getActivity(), GithubIconify.IconValue.octicon_repo_forked);
		branchesIcon.colorRes(R.color.white);
		branchesIcon.sizeRes(R.dimen.fab_size_mini_icon);
		fabBranches.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				GetRepoBranchesClient repoBranchesClient = new GetRepoBranchesClient(getActivity(), repoInfo);
				repoBranchesClient.setOnResultCallback(new DialogBranchesCallback(getActivity(), repoInfo) {

					@Override
					protected void onBranchSelected(String branch) {
						setCurrentBranch(branch);
					}
				});
				repoBranchesClient.execute();
				fabMenu.collapse();
			}
		});

		fabBranches.setIconDrawable(branchesIcon);

		fabUp = (FloatingActionButton) view.findViewById(R.id.fab_up);
		GithubIconDrawable upIcon = new GithubIconDrawable(getActivity(), GithubIconify.IconValue.octicon_arrow_up);
		upIcon.colorRes(R.color.white);
		upIcon.sizeRes(R.dimen.fab_size_mini_icon);
		fabUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				currentSelectedContent = currentSelectedContent.parent;
				if (treeContent.get(currentSelectedContent) != null) {
					displayContent(treeContent.get(currentSelectedContent));
				}
			}
		});

		fabUp.setIconDrawable(upIcon);
		fabUp.setVisibility(View.INVISIBLE);

		if (getArguments() != null) {

			getContent();

		}
	}

	@Override
	protected void loadArguments() {
		if (getArguments() != null) {
			repoInfo = getArguments().getParcelable(REPO_INFO);
		}
	}

	private void getContent() {
		rootContent = new Content();
		currentSelectedContent = rootContent;

		treeContent = new HashMap<>();
		treeContent.put(currentSelectedContent, null);

		if (contentAdapter != null) {
			contentAdapter.clear();
		}

		contentAdapter = null;
		
		fabUp.setVisibility(View.INVISIBLE);
		fabMenu.collapse();
		
		GetRepoContentsClient repoContentsClient = new GetRepoContentsClient(getActivity(), repoInfo);
		repoContentsClient.setOnResultCallback(this);
		repoContentsClient.execute();
	}

	@Override
	protected GithubIconify.IconValue getNoDataIcon() {
		return GithubIconify.IconValue.octicon_file_text;
	}

	@Override
	protected int getNoDataText() {
		return 0;
	}

	@Override
	public void onResponseOk(ListContents contents, Response r) {

		Collections.sort(contents, Content.Comparators.TYPE);
		treeContent.put(currentSelectedContent, contents);

		displayContent(contents);
	}

	private void displayContent(ListContents contents) {
		if (getActivity() != null) {
			stopRefresh();

			if (currentSelectedContent.parent != null) {
				fabUp.setVisibility(View.VISIBLE);
				fabMenu.expand();
			} else {
				fabUp.setVisibility(View.INVISIBLE);
				fabMenu.collapse();
			}

			int style = R.style.AppTheme_Repos;
			contentAdapter = new RepoSourceAdapter(getActivity(), contents, style);
			setListAdapter(contentAdapter);
		}
	}

	@Override
	public void onFail(RetrofitError error) {
		if (getActivity() != null) {
			ErrorHandler.onRetrofitError(getActivity(), "FilesTreeFragment", error);
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		if (contentAdapter != null && contentAdapter.getCount() >= position) {
			Content item = contentAdapter.getItem(position);
			if (item.isDir()) {
				if (treeContent.get(item) == null) {
					item.parent = currentSelectedContent;

					currentSelectedContent = item;
					getPathContent(item);
				} else {
					currentSelectedContent = item;
					displayContent(treeContent.get(item));
				}
			} else if (item.isFile()) {
				Intent intent = FileActivity.createLauncherIntent(getActivity(), repoInfo, item.name, item.path);
				startActivity(intent);
			}
		}
	}

	public void setCurrentBranch(String branch) {
		repoInfo.branch = branch;
		getContent();
	}

	private void getPathContent(Content item) {
		startRefresh();
		GetRepoContentsClient repoContentsClient = new GetRepoContentsClient(getActivity(), repoInfo, item.path);
		repoContentsClient.setOnResultCallback(this);
		repoContentsClient.execute();
	}

	@Override
	public CharSequence getTitle() {
		return getString(R.string.files_fragment_title);
	}

	@Override
	protected boolean useFAB() {
		return false;
	}

	@Override
	public void onRefresh() {
		getContent();
	}

	private class DownloadBranchesCallback extends DialogBranchesCallback {

		public DownloadBranchesCallback(Context context, RepoInfo repoInfo) {
			super(context, repoInfo);
		}

		@Override
		protected void onBranchSelected(String branch) {
			Toast.makeText(getContext(), R.string.code_download, Toast.LENGTH_LONG).show();

			String downloadFileType = new GitskariosSettings(getContext()).getDownloadFileType();
			RepoInfo repoInfo = new RepoInfo();
			repoInfo.owner = getRepoInfo().owner;
			repoInfo.name = getRepoInfo().name;
			repoInfo.branch = branch;
			GetArchiveLinkService getArchiveLinkService = new GetArchiveLinkService(getContext(), repoInfo, downloadFileType);
			getArchiveLinkService.execute();
		}
	}
}

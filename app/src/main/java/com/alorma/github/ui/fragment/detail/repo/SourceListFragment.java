package com.alorma.github.ui.fragment.detail.repo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Branch;
import com.alorma.github.sdk.bean.dto.response.Content;
import com.alorma.github.sdk.bean.dto.response.ContentType;
import com.alorma.github.sdk.bean.dto.response.ListContents;
import com.alorma.github.sdk.bean.dto.response.UpContent;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.content.GetArchiveLinkService;
import com.alorma.github.sdk.services.repo.GetRepoBranchesClient;
import com.alorma.github.sdk.services.repo.GetRepoContentsClient;
import com.alorma.github.sdk.utils.GitskariosSettings;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.activity.FileActivity;
import com.alorma.github.ui.adapter.detail.repo.RepoSourceAdapter;
import com.alorma.github.ui.callbacks.DialogBranchesCallback;
import com.alorma.github.ui.fragment.base.LoadingListFragment;
import com.alorma.github.ui.listeners.TitleProvider;
import com.alorma.githubicons.GithubIconify;
import com.crashlytics.android.Crashlytics;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 20/07/2014.
 */
public class SourceListFragment extends LoadingListFragment implements BaseClient.OnResultCallback<ListContents>,
		BranchManager, TitleProvider {

	private static final String REPO_INFO = "REPO_INFO";

	private RepoInfo repoInfo;

	private RepoSourceAdapter contentAdapter;
	private Map<Content, ListContents> treeContent;
	private Content rootContent = new Content();
	private Content currentSelectedContent = rootContent;

	public static SourceListFragment newInstance(RepoInfo repoInfo) {
		Bundle bundle = new Bundle();
		bundle.putParcelable(REPO_INFO, repoInfo);

		SourceListFragment f = new SourceListFragment();
		f.setArguments(bundle);
		return f;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		view.setBackgroundColor(getResources().getColor(R.color.gray_github_light));

		if (getArguments() != null) {

			getContent();

			treeContent = new HashMap<>();
			treeContent.put(currentSelectedContent, null);
		}
	}

	@Override
	protected void loadArguments() {
		if (getArguments() != null) {
			repoInfo = getArguments().getParcelable(REPO_INFO);
		}
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
		displayContent(contents);
	}

	private void displayContent(ListContents contents) {
		stopRefresh();
		if (getActivity() != null && contents != null) {
			try {
				ListContents currentContents = treeContent.get(currentSelectedContent);

				Context context = getActivity();
				int style = R.style.AppTheme_Repos;
				if (currentContents == null) {
					int size = contents.size() + (currentSelectedContent.parent != null ? 1 : 0);
					currentContents = new ListContents(size);
					treeContent.put(currentSelectedContent, currentContents);
					if (currentSelectedContent.parent != null) {
						Content up = new UpContent();
						up.parent = currentSelectedContent.parent;
						currentContents.add(up);
					}

					Collections.sort(contents, ListContents.SORT.TYPE);
					currentContents.addAll(contents);

					contentAdapter = new RepoSourceAdapter(context, currentContents, style);
				} else {
					contentAdapter = new RepoSourceAdapter(context, contents, style);
				}

				setListAdapter(contentAdapter);

			} catch (Exception e) {
				Crashlytics.logException(e);
			}
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
					treeContent.put(item, null);

					getPathContent(item);
				} else {
					displayContent(treeContent.get(item));
				}

			} else if (item.isFile()) {
				Intent intent = FileActivity.createLauncherIntent(getActivity(), repoInfo, item.name, item.path);
				startActivity(intent);
			} else if (ContentType.up.equals(item.type)) {
				if (item.parent != null) {
					currentSelectedContent = item.parent;
					displayContent(treeContent.get(currentSelectedContent));
				}
			}
		}
	}

	@Override
	public void setCurrentBranch(Branch branch) {
		rootContent = new Content();
		currentSelectedContent = rootContent;

		treeContent = new HashMap<>();
		treeContent.put(currentSelectedContent, null);

		contentAdapter.clear();

		contentAdapter = null;

		getContent();
	}

	private void getContent() {
		GetRepoContentsClient repoContentsClient = new GetRepoContentsClient(getActivity(), repoInfo);
		repoContentsClient.setOnResultCallback(this);
		repoContentsClient.execute();
	}

	private void getPathContent(Content item) {
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
		return true;
	}

	@Override
	protected GithubIconify.IconValue getFABGithubIcon() {
		return GithubIconify.IconValue.octicon_cloud_download;
	}

	@Override
	protected void fabClick() {
		GetRepoBranchesClient repoBranchesClient = new GetRepoBranchesClient(getActivity(), repoInfo);
		repoBranchesClient.setOnResultCallback(new DownloadBranchesCallback(getActivity(), repoInfo));
		repoBranchesClient.execute();
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

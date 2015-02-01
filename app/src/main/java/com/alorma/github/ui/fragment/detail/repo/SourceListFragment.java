package com.alorma.github.ui.fragment.detail.repo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Branch;
import com.alorma.github.sdk.bean.dto.response.Content;
import com.alorma.github.sdk.bean.dto.response.ContentType;
import com.alorma.github.sdk.bean.dto.response.ListBranches;
import com.alorma.github.sdk.bean.dto.response.ListContents;
import com.alorma.github.sdk.bean.dto.response.UpContent;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.content.GetArchiveLinkService;
import com.alorma.github.sdk.services.repo.GetRepoBranchesClient;
import com.alorma.github.sdk.services.repo.GetRepoContentsClient;
import com.alorma.github.sdk.utils.GitskariosSettings;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.activity.FileActivity;
import com.alorma.github.ui.adapter.detail.repo.RepoSourceAdapter;
import com.alorma.github.ui.fragment.base.BaseListFragment;
import com.alorma.github.ui.listeners.RefreshListener;
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
public class SourceListFragment extends BaseListFragment implements BaseClient.OnResultCallback<ListContents>,
		BranchManager, TitleProvider {

	public static final String OWNER = "OWNER";
	public static final String REPO = "REPO";
	public static final String BRANCH = "BRANCH";
	private String owner;
	private String repo;
	private RepoSourceAdapter contentAdapter;
	private RefreshListener refreshListener;
	private Map<Content, ListContents> treeContent;
	private Content rootContent = new Content();
	private Content currentSelectedContent = rootContent;
	private Branch currentBranch;
	private String branch;

	public static SourceListFragment newInstance(String owner, String repo, String branchName, RefreshListener refreshListener) {
		Bundle bundle = new Bundle();
		bundle.putString(OWNER, owner);
		bundle.putString(REPO, repo);
		bundle.putString(BRANCH, branchName);

		SourceListFragment f = new SourceListFragment();
		f.setRefreshListener(refreshListener);
		f.setArguments(bundle);
		return f;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		view.setBackgroundColor(getResources().getColor(R.color.gray_github_light));

		if (getArguments() != null) {
			owner = getArguments().getString(OWNER);
			repo = getArguments().getString(REPO);
			branch = getArguments().getString(BRANCH);

			getContent();

			treeContent = new HashMap<Content, ListContents>();
			treeContent.put(currentSelectedContent, null);

			if (refreshListener != null) {
				refreshListener.showRefresh();
			}
		}
	}

	@Override
	protected void loadArguments() {

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

				if (refreshListener != null) {
					refreshListener.cancelRefresh();
				}
			} catch (Exception e) {
				Crashlytics.logException(e);
			}
		}
	}

	@Override
	public void onFail(RetrofitError error) {
		if (getActivity() != null) {
			ErrorHandler.onRetrofitError(getActivity(), "FilesTreeFragment", error);
			if (refreshListener != null) {
				refreshListener.cancelRefresh();
			}
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		if (contentAdapter != null && contentAdapter.getCount() >= position) {
			Content item = contentAdapter.getItem(position);
			if (item.isDir()) {
				if (refreshListener != null) {
					refreshListener.showRefresh();
				}

				if (treeContent.get(item) == null) {
					item.parent = currentSelectedContent;

					currentSelectedContent = item;
					treeContent.put(item, null);

					getPathContent(item);
				} else {
					displayContent(treeContent.get(item));
				}

			} else if (item.isFile()) {
				Intent intent = FileActivity.createLauncherIntent(getActivity(), owner,
						repo, currentBranch != null ? currentBranch.name : null, item.name, item.path);
				startActivity(intent);
			} else if (ContentType.up.equals(item.type)) {
				if (item.parent != null) {
					currentSelectedContent = item.parent;
					displayContent(treeContent.get(currentSelectedContent));
				}
			}
		}
	}

	public void setRefreshListener(RefreshListener refreshListener) {
		this.refreshListener = refreshListener;
	}

	@Override
	public void setCurrentBranch(Branch branch) {
		this.currentBranch = branch;

		rootContent = new Content();
		currentSelectedContent = rootContent;

		treeContent = new HashMap<Content, ListContents>();
		treeContent.put(currentSelectedContent, null);

		contentAdapter.clear();

		contentAdapter = null;

		getContent();

		if (refreshListener != null) {
			refreshListener.showRefresh();
		}
	}

	private void getContent() {
		if (branch == null) {
			GetRepoContentsClient repoContentsClient = new GetRepoContentsClient(getActivity(), owner, repo);
			repoContentsClient.setOnResultCallback(this);
			repoContentsClient.execute();
		} else {
			GetRepoContentsClient repoContentsClient = new GetRepoContentsClient(getActivity(), owner, repo);
			repoContentsClient.setOnResultCallback(this);
			Branch branch = new Branch();
			branch.name = this.branch;
			repoContentsClient.setCurrentBranch(branch);
			repoContentsClient.execute();
		}
	}

	private void getPathContent(Content item) {
		GetRepoContentsClient repoContentsClient = new GetRepoContentsClient(getActivity(), owner, repo, item.path);
		repoContentsClient.setOnResultCallback(this);
		repoContentsClient.setCurrentBranch(currentBranch);
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
		GetRepoBranchesClient repoBranchesClient = new GetRepoBranchesClient(getActivity(), owner, repo);
		repoBranchesClient.setOnResultCallback(new DownloadBranchesCallback());
		repoBranchesClient.execute();
	}

	private class DownloadBranchesCallback extends BranchesCallback {

		@Override
		public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
			materialDialog.dismiss();
			
			Toast.makeText(getActivity(), R.string.code_download, Toast.LENGTH_LONG).show();

			String downloadFileType = new GitskariosSettings(getActivity()).getDownloadFileType();
			GetArchiveLinkService getArchiveLinkService = new GetArchiveLinkService(getActivity(), owner, repo, branches.get(i).name, downloadFileType);
			getArchiveLinkService.execute();
		}
	}

	private abstract class BranchesCallback implements BaseClient.OnResultCallback<ListBranches>, MaterialDialog.ListCallback {

		protected ListBranches branches;

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
	}
}

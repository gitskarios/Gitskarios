package com.alorma.github.ui.fragment.detail.repo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearBreadcrumb;
import android.widget.ListView;
import android.widget.Toast;

import com.alorma.github.R;
import com.alorma.github.basesdk.client.BaseClient;
import com.alorma.github.sdk.bean.dto.response.Content;
import com.alorma.github.sdk.bean.info.FileInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.content.GetArchiveLinkService;
import com.alorma.github.sdk.services.repo.GetRepoBranchesClient;
import com.alorma.github.sdk.services.repo.GetRepoContentsClient;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.activity.FileActivity;
import com.alorma.github.ui.adapter.detail.repo.RepoSourceAdapter;
import com.alorma.github.ui.callbacks.DialogBranchesCallback;
import com.alorma.github.ui.fragment.base.LoadingListFragment;
import com.alorma.github.ui.listeners.TitleProvider;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 20/07/2014.
 */
public class SourceListFragment extends LoadingListFragment<RepoSourceAdapter> implements BaseClient.OnResultCallback<List<Content>>, TitleProvider, BranchManager
        , LinearBreadcrumb.SelectionCallback, PermissionsManager, BackManager {

    private static final String REPO_INFO = "REPO_INFO";

    private RepoInfo repoInfo;

    private LinearBreadcrumb breadCrumbs;
    private String currentPath;

    public static SourceListFragment newInstance(RepoInfo repoInfo) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(REPO_INFO, repoInfo);

        SourceListFragment f = new SourceListFragment();
        f.setArguments(bundle);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.source_list_fragment, null, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.setBackgroundColor(getResources().getColor(R.color.gray_github_light));


        breadCrumbs = (LinearBreadcrumb) view.findViewById(R.id.breadCrumbs);

        breadCrumbs.setCallback(this);

        if (getArguments() != null) {
            getContent();
        }
    }

    private void navigateUp() {
        if (currentPath != null) {
            String[] paths = currentPath.split("/");

            paths = Arrays.copyOf(paths, paths.length - 1);

            StringBuilder builder = new StringBuilder();
            if (paths.length > 0) {
                for (String path : paths) {
                    builder.append(path);
                    builder.append("/");

                }
                String path = builder.toString();
                getPathContent(path.substring(0, path.length() - 1));
            } else {
                getContent();
            }
        }
    }

    @Override
    protected void loadArguments() {
        if (getArguments() != null) {
            repoInfo = getArguments().getParcelable(REPO_INFO);
        }
    }

    private void getContent() {
        currentPath = "/";

        if (getAdapter() != null) {
            getAdapter().clear();
        }

        setAdapter(null);

        breadCrumbs.initRootCrumb();

        GetRepoContentsClient repoContentsClient = new GetRepoContentsClient(getActivity(), repoInfo);
        repoContentsClient.setOnResultCallback(this);
        repoContentsClient.execute();
    }

    @Override
    protected Octicons.Icon getNoDataIcon() {
        return Octicons.Icon.oct_file_text;
    }

    @Override
    protected int getNoDataText() {
        return R.string.no_content;
    }

    @Override
    public void onResponseOk(List<Content> contents, Response r) {
        if (getActivity() != null) {
            if (contents != null && contents.size() > 0) {

                Collections.sort(contents, Content.Comparators.TYPE);

                displayContent(contents);
            } else if (getAdapter() == null || getAdapter().getItemCount() == 0) {
                setEmpty();
            }
        }
    }

    @Override
    public void onFail(RetrofitError error) {
        if (getActivity() != null) {
            if (getAdapter() == null || getAdapter().getItemCount() == 0) {
                if (error != null && error.getResponse() != null) {
                    setEmpty(error.getResponse().getStatus());
                }
            }
            ErrorHandler.onError(getActivity(), "FilesTreeFragment", error);
        }
    }

    private void displayContent(List<Content> contents) {
        if (getActivity() != null) {
            stopRefresh();

            if (currentPath != null) {
                breadCrumbs.addPath(currentPath, "/");
            }

            RepoSourceAdapter contentAdapter = new RepoSourceAdapter(LayoutInflater.from(getActivity()));
            contentAdapter.addAll(contents);
            setAdapter(contentAdapter);
        }
    }

    // TODO
    /*@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (getAdapter() != null && getAdapter().getItemCount() >= position) {
            Content item = getAdapter().getItem(position);
            if (item.isDir()) {
                getPathContent(item.path);
            } else if (item.isFile()) {
                FileInfo info = new FileInfo();
                info.repoInfo = repoInfo;
                info.name = item.name;
                info.path = item.path;
                Intent intent = FileActivity.createLauncherIntent(getActivity(), info, false);
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (contentAdapter != null && contentAdapter.getCount() >= position) {
            Content item = contentAdapter.getItem(position);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(Intent.EXTRA_SUBJECT, repoInfo.owner + "/" + repoInfo.name);
            intent.putExtra(Intent.EXTRA_TEXT, item._links.html);
            startActivity(intent);
            return true;
        }

        return super.onItemLongClick(parent, view, position, id);
    }
    */

    @Override
    public void setEmpty(int statusCode) {
        super.setEmpty(statusCode);
    }

    @Override
    public void hideEmpty() {
        super.hideEmpty();
    }

    @Override
    public void setCurrentBranch(String branch) {
        repoInfo.branch = branch;
        getContent();
    }

    private void getPathContent(String path) {
        currentPath = path;
        startRefresh();
        GetRepoContentsClient repoContentsClient = new GetRepoContentsClient(getActivity(), repoInfo, path);
        repoContentsClient.setOnResultCallback(this);
        repoContentsClient.execute();
    }

    @Override
    public int getTitle() {
        return R.string.files_fragment_title;
    }

    @Override
    protected boolean useFAB() {
        return true;
    }

    @Override
    protected Octicons.Icon getFABGithubIcon() {
        return Octicons.Icon.oct_cloud_download;
    }

    @Override
    protected void fabClick() {
        super.fabClick();
        GetRepoBranchesClient repoBranchesClient = new GetRepoBranchesClient(getActivity(), repoInfo);
        repoBranchesClient.setOnResultCallback(new DownloadBranchesCallback(getActivity(), repoInfo));
        repoBranchesClient.execute();
    }

    @Override
    public void onRefresh() {
        if (currentPath == null || currentPath.equals("/")) {
            getContent();
        } else {
            getPathContent(currentPath);
        }
    }

    @Override
    public void onCrumbSelection(LinearBreadcrumb.Crumb crumb, String absolutePath, int count, int index) {
        if (crumb.getPath() != null && crumb.getPath().equals("/")) {
            getContent();
        } else {
            getPathContent(breadCrumbs.getAbsolutePath(crumb, "/"));
        }
        breadCrumbs.setActive(crumb);
    }

    @Override
    public void setPermissions(boolean admin, boolean push, boolean pull) {

    }

    private class DownloadBranchesCallback extends DialogBranchesCallback {

        public DownloadBranchesCallback(Context context, RepoInfo repoInfo) {
            super(context, repoInfo);
        }

        @Override
        protected void onNoBranches() {
            Toast.makeText(getContext(), R.string.no_branches_download, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onBranchSelected(String branch) {
            Toast.makeText(getContext(), R.string.code_download, Toast.LENGTH_LONG).show();

            RepoInfo repoInfo = new RepoInfo();
            repoInfo.owner = getRepoInfo().owner;
            repoInfo.name = getRepoInfo().name;
            repoInfo.branch = branch;
            GetArchiveLinkService getArchiveLinkService = new GetArchiveLinkService(getContext(), repoInfo);
            getArchiveLinkService.execute();
        }
    }

    @Override
    public boolean onBackPressed() {
        if (breadCrumbs.size() == 1) {
            return true;
        } else {
            navigateUp();
            return false;
        }
    }
}

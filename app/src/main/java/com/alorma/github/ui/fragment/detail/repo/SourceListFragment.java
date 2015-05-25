package com.alorma.github.ui.fragment.detail.repo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Content;
import com.alorma.github.sdk.bean.dto.response.ListContents;
import com.alorma.github.sdk.bean.info.FileInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.gitskarios.basesdk.client.BaseClient;
import com.alorma.github.sdk.services.content.GetArchiveLinkService;
import com.alorma.github.sdk.services.repo.GetRepoBranchesClient;
import com.alorma.github.sdk.services.repo.GetRepoContentsClient;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.activity.FileActivity;
import com.alorma.github.ui.adapter.detail.repo.RepoSourceAdapter;
import com.alorma.github.ui.callbacks.DialogBranchesCallback;
import com.alorma.github.ui.fragment.base.LoadingListFragment;
import com.alorma.github.ui.listeners.TitleProvider;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.mrengineer13.snackbar.SnackBar;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;

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
    private View snackView;
    private SnackBar branchSnackBar;
    private boolean expandedFab = false;

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
        IconicsDrawable downloadIcon = new IconicsDrawable(getActivity(), Octicons.Icon.oct_cloud_download);
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
        IconicsDrawable branchesIcon = new IconicsDrawable(getActivity(), Octicons.Icon.oct_repo_forked);
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
        IconicsDrawable upIcon = new IconicsDrawable(getActivity(), Octicons.Icon.oct_arrow_up);
        upIcon.colorRes(R.color.white);
        upIcon.sizeRes(R.dimen.fab_size_mini_icon);
        fabUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateUp();
            }
        });
        fabUp.setIconDrawable(upIcon);
        fabUp.setVisibility(View.INVISIBLE);


        FloatingActionButton fabRefresh = (FloatingActionButton) view.findViewById(R.id.fab_sync);
        IconicsDrawable refreshIcon = new IconicsDrawable(getActivity(), Octicons.Icon.oct_sync);
        refreshIcon.colorRes(R.color.white);
        refreshIcon.sizeRes(R.dimen.fab_size_mini_icon);
        fabRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getPathContent(currentSelectedContent);

                fabMenu.collapse();
            }
        });
        fabRefresh.setIconDrawable(refreshIcon);


        snackView = view.findViewById(R.id.snackBar);

        if (getArguments() != null) {
            getContent();
        }
    }

    private void navigateUp() {
        if (currentSelectedContent != null) {
            currentSelectedContent = currentSelectedContent.parent;
            if (currentSelectedContent != null) {
                if (treeContent.get(currentSelectedContent) != null) {
                    displayContent(treeContent.get(currentSelectedContent));
                }
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

        if (branchSnackBar == null) {
            branchSnackBar = new SnackBar.Builder(getActivity(), snackView).withMessage(repoInfo.branch).withDuration(SnackBar.PERMANENT_SNACK).show();
        }

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
    public void onResponseOk(ListContents contents, Response r) {
        if (getActivity() != null) {
            if (contents != null && contents.size() > 0) {
                Collections.sort(contents, Content.Comparators.TYPE);
                treeContent.put(currentSelectedContent, contents);

                displayContent(contents);
            } else if (contentAdapter == null || contentAdapter.getCount() == 0) {
                setEmpty();
            }
        }
    }

    @Override
    public void onFail(RetrofitError error) {
        if (getActivity() != null) {
            if (contentAdapter == null || contentAdapter.getCount() == 0) {
                setEmpty();
            }
            ErrorHandler.onError(getActivity(), "FilesTreeFragment", error);
        }
    }

    private void displayContent(ListContents contents) {
        if (getActivity() != null) {
            stopRefresh();

            if (currentSelectedContent.parent != null) {
                fabUp.setVisibility(View.VISIBLE);
                if (!expandedFab) {
                    expandedFab = true;
                    fabMenu.expand();
                }
                delayClose();
            } else {
                fabUp.setVisibility(View.INVISIBLE);
                fabMenu.collapse();
            }

            contentAdapter = new RepoSourceAdapter(getActivity(), contents);
            setListAdapter(contentAdapter);
        }
    }

    private void delayClose() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (fabMenu.isExpanded()) {
                    fabMenu.collapse();
                }
            }
        }, 2500);
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
                FileInfo info = new FileInfo();
                info.repoInfo = repoInfo;
                info.name = item.name;
                info.path = item.path;
                Intent intent = FileActivity.createLauncherIntent(getActivity(), info, expandedFab);
                startActivity(intent);
            }
        }
    }

    @Override
    public void setEmpty() {
        super.setEmpty();
        if (fabMenu != null) {
            fabMenu.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void hideEmpty() {
        super.hideEmpty();
        if (fabMenu != null) {
            fabMenu.setVisibility(View.VISIBLE);
        }
    }

    public void setCurrentBranch(String branch) {
        repoInfo.branch = branch;

        if (branchSnackBar != null) {
            branchSnackBar.clear();
            branchSnackBar = null;
        }
        branchSnackBar = new SnackBar.Builder(getActivity(), snackView).withMessage(repoInfo.branch).withDuration(SnackBar.PERMANENT_SNACK).show();
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

            RepoInfo repoInfo = new RepoInfo();
            repoInfo.owner = getRepoInfo().owner;
            repoInfo.name = getRepoInfo().name;
            repoInfo.branch = branch;
            GetArchiveLinkService getArchiveLinkService = new GetArchiveLinkService(getContext(), repoInfo);
            getArchiveLinkService.execute();
        }
    }

    public void onBackPressed() {
        navigateUp();
    }
}

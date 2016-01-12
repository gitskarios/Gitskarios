package com.alorma.github.ui.fragment.detail.repo;

import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alorma.github.GitskariosSettings;
import com.alorma.github.R;
import com.alorma.github.UrlsManager;
import com.alorma.github.sdk.bean.dto.response.Content;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.content.Downloader;
import com.alorma.github.sdk.services.content.GetArchiveLinkService;
import com.alorma.github.sdk.services.repo.GetRepoBranchesClient;
import com.alorma.github.sdk.services.repo.GetRepoContentsClient;
import com.alorma.github.ui.activity.ContentCommitsActivity;
import com.alorma.github.ui.adapter.detail.repo.RepoSourceAdapter;
import com.alorma.github.ui.callbacks.DialogBranchesCallback;
import com.alorma.github.ui.fragment.base.LoadingListFragment;
import com.alorma.github.ui.listeners.TitleProvider;
import com.alorma.github.ui.view.LinearBreadcrumb;
import com.alorma.gitskarios.core.Pair;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit.RetrofitError;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Bernat on 20/07/2014.
 */
public class SourceListFragment extends LoadingListFragment<RepoSourceAdapter>
        implements TitleProvider, BranchManager, LinearBreadcrumb.SelectionCallback, BackManager, RepoSourceAdapter.SourceAdapterListener {

    private static final String REPO_INFO = "REPO_INFO";

    private RepoInfo repoInfo;

    private LinearBreadcrumb breadCrumbs;
    private String currentPath;
    private Observer<Pair<List<Content>, Integer>> subscriber;

    public static SourceListFragment newInstance(RepoInfo repoInfo) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(REPO_INFO, repoInfo);

        SourceListFragment f = new SourceListFragment();
        f.setArguments(bundle);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        subscriber = new Observer<Pair<List<Content>, Integer>>() {
            @Override
            public void onCompleted() {
                stopRefresh();
            }

            @Override
            public void onError(Throwable e) {
                stopRefresh();
                if (getActivity() != null) {
                    if (getAdapter() == null || getAdapter().getItemCount() == 0) {
                        if (e != null && e instanceof RetrofitError && ((RetrofitError) e).getResponse() != null) {
                            setEmpty(true, ((RetrofitError) e).getResponse().getStatus());
                        }
                    }
                }
            }

            @Override
            public void onNext(Pair<List<Content>, Integer> listIntegerPair) {
                onContentLoaded(listIntegerPair.first);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.source_list_fragment, null, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

        GetRepoContentsClient repoContentsClient = new GetRepoContentsClient(repoInfo);
        repoContentsClient.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
    }

    @Override
    protected Octicons.Icon getNoDataIcon() {
        return Octicons.Icon.oct_file_text;
    }

    @Override
    protected int getNoDataText() {
        return R.string.no_content;
    }

    private void displayContent(List<Content> contents) {
        if (getActivity() != null) {
            stopRefresh();

            if (currentPath != null) {
                breadCrumbs.addPath(currentPath, "/");
            }

            RepoSourceAdapter contentAdapter = new RepoSourceAdapter(getActivity(), LayoutInflater.from(getActivity()));
            contentAdapter.setSourceAdapterListener(this);
            contentAdapter.addAll(contents);
            setAdapter(contentAdapter);
        }
    }

    @Override
    public void onContentClick(Content item) {
        if (item.isDir()) {
            getPathContent(item.path);
        } else if (item.isFile()) {
            Intent intent = new UrlsManager(getActivity()).checkUri(Uri.parse(item._links.html));
            startActivity(intent);
        }
    }

    @Override
    public void onContentMenuAction(Content content, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_content_share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, repoInfo.owner + "/" + repoInfo.name);
                shareIntent.putExtra(Intent.EXTRA_TEXT, content._links.html);
                startActivity(shareIntent);
                break;
            case R.id.action_content_open:
                Intent viewIntent = new Intent(Intent.ACTION_VIEW);
                viewIntent.setData(Uri.parse(content._links.html));
                viewIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(viewIntent);
                break;
            case R.id.action_copy_content_url:
                copy(content._links.html);
                Toast.makeText(getActivity(), getString(R.string.url_of_copied, content.name), Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_content_history:
                Intent intent = ContentCommitsActivity.createLauncherIntent(getActivity(), repoInfo, content.path, content.name);
                startActivity(intent);
                break;
        }
    }

    public void copy(String text) {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Gitskarios", text);
        clipboard.setPrimaryClip(clip);
    }

    @Override
    public void setEmpty(boolean withError, int statusCode) {
        super.setEmpty(withError, statusCode);
    }

    @Override
    public void hideEmpty() {
        super.hideEmpty();
    }

    @Override
    public void setCurrentBranch(String branch) {
        loadArguments();
        repoInfo.branch = branch;
        getContent();
    }

    private void getPathContent(String path) {
        currentPath = path;
        startRefresh();

        GetRepoContentsClient repoContentsClient = new GetRepoContentsClient(repoInfo, path);
        repoContentsClient.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
    }

    private void onContentLoaded(List<Content> contents) {
        if (getActivity() != null) {
            if (contents != null && contents.size() > 0) {
                hideEmpty();
                Collections.sort(contents);

                Collections.reverse(contents);

                displayContent(contents);
            } else if (getAdapter() == null || getAdapter().getItemCount() == 0) {
                setEmpty();
            }
        }
    }

    @Override
    public int getTitle() {
        return R.string.files_fragment_title;
    }

    @Override
    public IIcon getTitleIcon() {
        return Octicons.Icon.oct_file_directory;
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
        GetRepoBranchesClient repoBranchesClient = new GetRepoBranchesClient(repoInfo);
        repoBranchesClient.observable().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DownloadBranchesCallback(getActivity(), repoInfo));
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
    public void loadMoreItems() {

    }

    @Override
    public boolean onBackPressed() {
        if (breadCrumbs != null && breadCrumbs.size() == 1) {
            return true;
        } else {
            navigateUp();
            return false;
        }
    }

    private class DownloadBranchesCallback extends DialogBranchesCallback {

        public DownloadBranchesCallback(Context context, RepoInfo repoInfo) {
            super(context, repoInfo);
        }

        @Override
        protected void onNoBranches() {
            RepoInfo repoInfo = new RepoInfo();
            repoInfo.owner = getRepoInfo().owner;
            repoInfo.name = getRepoInfo().name;
            download(repoInfo);
        }

        @Override
        protected void onBranchSelected(String branch) {
            Toast.makeText(getContext(), R.string.code_download, Toast.LENGTH_LONG).show();

            RepoInfo repoInfo = new RepoInfo();
            repoInfo.owner = getRepoInfo().owner;
            repoInfo.name = getRepoInfo().name;
            repoInfo.branch = branch;
            download(repoInfo);
        }

        private void download(RepoInfo repoInfo) {
            GitskariosSettings settings = new GitskariosSettings(getActivity());
            String zipBall = getActivity().getString(R.string.download_zip_value);
            String fileType = settings.getDownloadFileType(zipBall);
            GetArchiveLinkService getArchiveLinkService = new GetArchiveLinkService(repoInfo, fileType
                    , new NativeDownloader());
            getArchiveLinkService.observable().subscribeOn(Schedulers.io()).subscribe();
        }
    }

    class NativeDownloader implements Downloader {

        public NativeDownloader() {
        }

        @Override
        public void download(String path) {
            DownloadManager dm = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(path));

            GitskariosSettings settings = new GitskariosSettings(getActivity());

            String zipBall = getActivity().getString(R.string.download_zip_value);
            String fileType = settings.getDownloadFileType(zipBall);

            String fileName = repoInfo.name + "_" + repoInfo.branch + "_" + "." + (fileType.equalsIgnoreCase(zipBall) ? "zip" : "tar");
            request.setTitle(fileName);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "gitskarios/" + fileName);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.allowScanningByMediaScanner();
            dm.enqueue(request);
        }
    }
}

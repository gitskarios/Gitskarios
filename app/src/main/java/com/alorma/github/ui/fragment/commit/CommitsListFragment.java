package com.alorma.github.ui.fragment.commit;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialcab.MaterialCab;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Commit;
import com.alorma.github.sdk.bean.info.CommitInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.GithubListClient;
import com.alorma.github.sdk.services.commit.ListCommitsClient;
import com.alorma.github.sdk.services.repo.GetRepoBranchesClient;
import com.alorma.github.ui.activity.CommitDetailActivity;
import com.alorma.github.ui.activity.CompareRepositoryCommitsActivity;
import com.alorma.github.ui.adapter.commit.CommitsAdapter;
import com.alorma.github.ui.callbacks.DialogBranchesCallback;
import com.alorma.github.ui.fragment.base.LoadingListFragment;
import com.alorma.github.ui.fragment.detail.repo.BackManager;
import com.alorma.github.ui.fragment.detail.repo.BranchManager;
import com.alorma.github.ui.fragment.detail.repo.PermissionsManager;
import com.alorma.github.ui.listeners.TitleProvider;
import com.alorma.gitskarios.core.Pair;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Bernat on 07/09/2014.
 */
public class CommitsListFragment extends LoadingListFragment<CommitsAdapter>
        implements TitleProvider, BranchManager, PermissionsManager, BackManager, CommitsAdapter.CommitsAdapterListener, MaterialCab.Callback,
        Observer<List<Commit>> {

    private static final String REPO_INFO = "REPO_INFO";
    private static final String PATH = "PATH";

    private RepoInfo repoInfo;
    private String path;
    private boolean isInCompareMode = false;
    private String baseCompare = null;
    private String headCompare = null;
    private MaterialCab cab;

    public static CommitsListFragment newInstance(RepoInfo repoInfo) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(REPO_INFO, repoInfo);

        CommitsListFragment fragment = new CommitsListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static CommitsListFragment newInstance(RepoInfo repoInfo, String path) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(REPO_INFO, repoInfo);
        bundle.putString(PATH, path);

        CommitsListFragment fragment = new CommitsListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void loadArguments() {
        if (getArguments() != null) {
            repoInfo = (RepoInfo) getArguments().getParcelable(REPO_INFO);
            path = getArguments().getString(PATH);
        }
    }

    @Override
    protected void executeRequest() {
        super.executeRequest();
        CommitInfo commitInfo = new CommitInfo();
        commitInfo.repoInfo = repoInfo;
        commitInfo.sha = repoInfo.branch;
        setAction(new ListCommitsClient(commitInfo, path, 0));
    }

    private void setAction(final GithubListClient<List<Commit>> listCommitsClient) {
        listCommitsClient.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).map(new Func1<Pair<List<Commit>, Integer>, List<Commit>>() {
            @Override
            public List<Commit> call(Pair<List<Commit>, Integer> listIntegerPair) {
                setPage(listIntegerPair.second);
                return listIntegerPair.first;
            }
        }).map(orderCommits()).subscribe(this);
    }

    @Override
    protected void executePaginatedRequest(int page) {
        super.executePaginatedRequest(page);
        CommitInfo commitInfo = new CommitInfo();
        commitInfo.repoInfo = repoInfo;

        setAction(new ListCommitsClient(commitInfo, path, page));
    }

    @Override
    public void onCompleted() {
        stopRefresh();
    }

    @Override
    public void onError(Throwable e) {
        stopRefresh();
        if (getAdapter() == null || getAdapter().getItemCount() == 0) {
            setEmpty();
        }
    }

    @Override
    public void onNext(List<Commit> commits) {
        if (commits.size() > 0) {
            hideEmpty();

            if (refreshing || getAdapter() == null) {
                CommitsAdapter commitsAdapter = new CommitsAdapter(LayoutInflater.from(getActivity()), false, repoInfo);
                commitsAdapter.addAll(commits);
                commitsAdapter.setCommitsAdapterListener(this);
                setAdapter(commitsAdapter);
            } else {
                getAdapter().addAll(commits);
            }

            removeDecorations();

            StickyRecyclerHeadersDecoration headersDecoration =
                    new StickyRecyclerHeadersDecoration(getAdapter());
            addItemDecoration(headersDecoration);
        } else if (getAdapter() == null || getAdapter().getItemCount() == 0) {
            setEmpty();
        } else {
            getAdapter().clear();
            setEmpty();
        }
    }

    private Func1<List<Commit>, List<Commit>> orderCommits() {
        return new Func1<List<Commit>, List<Commit>>() {
            @Override
            public List<Commit> call(List<Commit> commits) {
                List<Commit> newCommits = new ArrayList<>();
                for (Commit commit : commits) {
                    if (commit.commit.author.date != null) {
                        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
                        DateTime dt = formatter.parseDateTime(commit.commit.committer.date);

                        Days days = Days.daysBetween(dt.withTimeAtStartOfDay(), new DateTime(System.currentTimeMillis()).withTimeAtStartOfDay());

                        commit.days = days.getDays();

                        newCommits.add(commit);
                    }
                }
                return newCommits;
            }
        };
    }

    @Override
    public void setEmpty(boolean withError, int statusCode) {
        super.setEmpty(withError, statusCode);
        if (fab != null) {
            fab.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void hideEmpty() {
        super.hideEmpty();
        if (fab != null) {
            fab.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setPermissions(boolean admin, boolean push, boolean pull) {

    }

    @Override
    public boolean onBackPressed() {
        if (cab != null && cab.isActive()) {
            cab.finish();
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected Octicons.Icon getNoDataIcon() {
        return Octicons.Icon.oct_diff;
    }

    @Override
    protected int getNoDataText() {
        return R.string.no_commits;
    }

    @Override
    public int getTitle() {
        return R.string.commits_fragment_title;
    }

    @Override
    public IIcon getTitleIcon() {
        return Octicons.Icon.oct_git_commit;
    }

    @Override
    protected boolean useFAB() {
        return !isInCompareMode;
    }

    @Override
    protected Octicons.Icon getFABGithubIcon() {
        return Octicons.Icon.oct_git_compare;
    }

    @Override
    protected void fabClick() {
        isInCompareMode = !isInCompareMode;
        checkFAB();
        if (getActivity() != null && getActivity() instanceof AppCompatActivity) {
            if (getActivity().findViewById(R.id.cab_stub) != null) {
                cab = new MaterialCab((AppCompatActivity) getActivity(), R.id.cab_stub).setTitle(":base ... :head")
                        .setMenu(R.menu.menu_commits_compare)
                        .start(this);

                if (cab.getMenu() != null) {
                    MenuItem itemCompare = cab.getMenu().findItem(R.id.action_compare_commits);

                    if (itemCompare != null) {
                        IconicsDrawable iconicsDrawable =
                                new IconicsDrawable(getActivity(), Octicons.Icon.oct_git_compare).actionBar().color(Color.WHITE);
                        itemCompare.setIcon(iconicsDrawable);
                        itemCompare.setEnabled(false);
                    }

                    MenuItem itemChangeBranch = cab.getMenu().findItem(R.id.action_repo_change_branch);

                    if (itemChangeBranch != null) {
                        IconicsDrawable iconicsDrawable =
                                new IconicsDrawable(getActivity(), Octicons.Icon.oct_git_branch).actionBar().color(Color.WHITE);
                        itemChangeBranch.setIcon(iconicsDrawable);
                    }
                }
            }
        }
    }

    @Override
    public void setCurrentBranch(String branch) {
        if (repoInfo != null) {
            repoInfo.branch = branch;

            if (getAdapter() != null) {
                getAdapter().clear();
            }
            startRefresh();
            refreshing = true;
            executeRequest();
        }
    }

    @Override
    public void onCommitClick(Commit commit) {
        if (!isInCompareMode) {
            CommitInfo info = new CommitInfo();
            info.repoInfo = repoInfo;
            info.sha = commit.sha;

            Intent intent = CommitDetailActivity.launchIntent(getActivity(), info);
            startActivity(intent);
        } else if (headCompare == null) {
            checkFAB();
            headCompare = commit.shortSha();
            cab.setTitle(headCompare + " ... :head");
        } else if (baseCompare == null) {
            checkFAB();
            baseCompare = commit.shortSha();
            cab.setTitle(headCompare + " ... " + baseCompare);
            if (cab.getMenu() != null) {
                MenuItem itemCompare = cab.getMenu().findItem(R.id.action_compare_commits);

                if (itemCompare != null) {
                    itemCompare.setEnabled(true);
                }
            }
        }
    }

    @Override
    public boolean onCommitLongClick(Commit commit) {
        copy(commit.shortSha());
        Toast.makeText(getActivity(), getString(R.string.sha_copied, commit.shortSha()), Toast.LENGTH_SHORT).show();
        return true;
    }

    public void copy(String text) {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Gitskarios", text);
        clipboard.setPrimaryClip(clip);
    }

    @Override
    public boolean onCabCreated(MaterialCab materialCab, Menu menu) {
        return true;
    }

    @Override
    public boolean onCabItemClicked(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.action_compare_commits) {
            isInCompareMode = false;
            if (cab.isActive()) {
                cab.finish();
            }
            Intent intent = CompareRepositoryCommitsActivity.launcherIntent(getActivity(), repoInfo, baseCompare, headCompare);
            startActivity(intent);
            baseCompare = null;
            headCompare = null;
        } else if (menuItem.getItemId() == R.id.action_repo_change_branch) {
            changeBranch();
        }
        return false;
    }

    private void changeBranch() {
        GetRepoBranchesClient repoBranchesClient = new GetRepoBranchesClient(repoInfo);
        repoBranchesClient.observable().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DialogBranchesCallback(getActivity(), repoInfo) {
                    @Override
                    protected void onBranchSelected(String branch) {
                        setCurrentBranch(branch);
                    }

                    @Override
                    protected void onNoBranches() {

                    }
                });
    }

    @Override
    public boolean onCabFinished(MaterialCab materialCab) {
        isInCompareMode = false;
        checkFAB();
        return true;
    }
}

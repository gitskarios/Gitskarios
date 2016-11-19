package com.alorma.github.ui.fragment.commit;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.CommitInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.GithubListClient;
import com.alorma.github.sdk.services.commit.ListCommitsClient;
import com.alorma.github.ui.activity.CompareRepositoryCommitsActivity;
import com.alorma.github.ui.adapter.commit.CommitsAdapter;
import com.alorma.github.ui.fragment.base.LoadingListFragment;
import com.alorma.github.ui.fragment.detail.repo.BackManager;
import com.alorma.github.ui.fragment.detail.repo.BranchManager;
import com.alorma.github.ui.fragment.detail.repo.PermissionsManager;
import com.alorma.github.utils.AttributesUtils;
import com.alorma.gitskarios.core.Pair;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import core.repositories.Commit;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class CommitsListFragment extends LoadingListFragment<CommitsAdapter>
    implements BranchManager, PermissionsManager, BackManager, CommitsAdapter.CommitsAdapterListener,
    Observer<List<Commit>> {

  private static final String REPO_INFO = "REPO_INFO";
  private static final String PATH = "PATH";

  private RepoInfo repoInfo;
  private String path;
  private boolean isInCompareMode = false;
  private String baseCompare = null;
  private String headCompare = null;

  private CommitSelectedCallback commitSelectedCallback;

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
      repoInfo = getArguments().getParcelable(REPO_INFO);
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
    listCommitsClient.observable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map((Func1<Pair<List<Commit>, Integer>, List<Commit>>) listIntegerPair -> {
          setPage(listIntegerPair.second);
          return listIntegerPair.first;
        })
        .map(orderCommits())
        .subscribe(this);
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    if (context instanceof CommitSelectedCallback) {
      commitSelectedCallback = (CommitSelectedCallback) context;
    }
  }

  @Override
  public void onDetach() {
    commitSelectedCallback = commit -> {

    };
    super.onDetach();
  }

  @Override
  protected int getLightTheme() {
    return R.style.AppTheme_Repository;
  }

  @Override
  protected int getDarkTheme() {
    return R.style.AppTheme_Dark_Repository;
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
        CommitsAdapter commitsAdapter = new CommitsAdapter(LayoutInflater.from(getActivity()), false);
        commitsAdapter.addAll(commits);
        commitsAdapter.setCallback(item -> {
          if (!isInCompareMode) {
            commitSelectedCallback.onCommitSelected(item);
          } else {
            selectCommit(item);
          }
        });
        commitsAdapter.setCommitsAdapterListener(this);
        setAdapter(commitsAdapter);
      } else {
        getAdapter().addAll(commits);
      }

      removeDecorations();

      StickyRecyclerHeadersDecoration headersDecoration = new StickyRecyclerHeadersDecoration(getAdapter());
      addItemDecoration(headersDecoration);
    } else if (getAdapter() == null || getAdapter().getItemCount() == 0) {
      setEmpty();
    } else {
      getAdapter().clear();
      setEmpty();
    }
  }

  private void selectCommit(Commit item) {
    if (baseCompare == null) {
      baseCompare = item.shortSha();
    } else {
      headCompare = item.shortSha();
    }

    if (extraToolbar != null) {
      if (baseCompare != null ) {
        MenuItem menuItem = extraToolbar.getMenu().findItem(R.id.action_compare_commits);
        if (menuItem != null) {
          menuItem.setEnabled(baseCompare != null && headCompare != null);
        }
      }

      extraToolbar.setTitle(baseCompare + " ... " + (headCompare != null ? headCompare : ":head"));
    }
  }

  private Func1<List<Commit>, List<Commit>> orderCommits() {
    return commits -> {
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
    if (extraToolbar != null && extraToolbar.getVisibility() == View.VISIBLE) {
      hideExtraToolbar();
      isInCompareMode = false;
      checkFAB();
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
    if (getActivity() != null) {
      showExtraToolbar();
      extraToolbar.setTitle(":base ... :head");
      Menu menu = extraToolbar.getMenu();
      if (menu != null) {
        menu.clear();
      }
      extraToolbar.inflateMenu(R.menu.menu_commits_compare);

      extraToolbar.setOnMenuItemClickListener(this::onExtraMenuItemSelected);

      if (extraToolbar.getMenu() != null) {
        prepareCompareMenu(extraToolbar.getMenu());
      }
    }
  }

  private boolean onExtraMenuItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_compare_commits) {
      isInCompareMode = false;
      if (extraToolbar.getVisibility() == View.VISIBLE) {
        hideExtraToolbar();
        checkFAB();
      }
      Intent intent = CompareRepositoryCommitsActivity.launcherIntent(getActivity(), repoInfo, baseCompare, headCompare);
      startActivity(intent);
      baseCompare = null;
      headCompare = null;
    }
    return false;
  }

  private void prepareCompareMenu(Menu menu) {
    int primaryColor = AttributesUtils.getPrimaryColor(getActivity());

    MenuItem itemCompare = menu.findItem(R.id.action_compare_commits);

    if (itemCompare != null) {
      IconicsDrawable iconicsDrawable = new IconicsDrawable(getActivity(), Octicons.Icon.oct_git_compare).actionBar().color(primaryColor);
      itemCompare.setIcon(iconicsDrawable);
      itemCompare.setEnabled(false);
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

  public interface CommitSelectedCallback {
    void onCommitSelected(Commit commit);
  }
}

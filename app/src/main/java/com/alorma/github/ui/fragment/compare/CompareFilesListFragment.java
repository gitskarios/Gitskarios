package com.alorma.github.ui.fragment.compare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.adapter.commit.CommitFilesAdapter;
import com.alorma.github.ui.fragment.base.LoadingListFragment;
import com.mikepenz.octicons_typeface_library.Octicons;
import core.repositories.CommitFile;
import java.util.List;

public class CompareFilesListFragment extends LoadingListFragment<CommitFilesAdapter> {

  private static final String REPO_INFO = "REPO_INFO";

  public static CompareFilesListFragment newInstance(RepoInfo repoInfo) {
    Bundle bundle = new Bundle();
    bundle.putParcelable(REPO_INFO, repoInfo);

    CompareFilesListFragment fragment = new CompareFilesListFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_compare_commits, null, false);
  }

  public void setFiles(List<CommitFile> commitFiles) {
    CommitFilesAdapter adapter = new CommitFilesAdapter(LayoutInflater.from(getActivity()));
    adapter.addAll(commitFiles);
    setAdapter(adapter);
    stopRefresh();
    hideEmpty();
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
  protected void loadArguments() {
    RepoInfo repoInfo = (RepoInfo) getArguments().getParcelable(REPO_INFO);
  }

  @Override
  protected int getLightTheme() {
    return R.style.AppTheme_Repository;
  }

  @Override
  protected int getDarkTheme() {
    return R.style.AppTheme_Dark_Repository;
  }
}

package com.alorma.github.ui.fragment.compare;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Commit;
import com.alorma.github.sdk.bean.dto.response.CommitFile;
import com.alorma.github.sdk.bean.info.CommitInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.activity.CommitDetailActivity;
import com.alorma.github.ui.adapter.commit.CommitFilesAdapter;
import com.alorma.github.ui.adapter.commit.CommitsAdapter;
import com.alorma.github.ui.fragment.base.LoadingListFragment;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.List;

/**
 * Created by Bernat on 07/09/2014.
 */
public class CompareFilesListFragment extends LoadingListFragment<CommitFilesAdapter> implements CommitsAdapter.CommitsAdapterListener {

    private static final String REPO_INFO = "REPO_INFO";
    private RepoInfo repoInfo;

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
        repoInfo = (RepoInfo) getArguments().getParcelable(REPO_INFO);
    }

    @Override
    protected boolean useFAB() {
        return false;
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void loadMoreItems() {

    }

    @Override
    public void onCommitClick(Commit commit) {
        CommitInfo info = new CommitInfo();
        info.repoInfo = repoInfo;
        info.sha = commit.sha;

        Intent intent = CommitDetailActivity.launchIntent(getActivity(), info);
        startActivity(intent);
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
}

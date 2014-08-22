package com.alorma.github.ui.adapter.detail.repo;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.alorma.github.sdk.bean.dto.response.Branch;
import com.alorma.github.ui.fragment.detail.repo.FilesTreeFragment;
import com.alorma.github.ui.fragment.detail.repo.MarkdownFragment;
import com.alorma.github.ui.fragment.issues.IssuesFragment;
import com.alorma.github.ui.listeners.RefreshListener;

/**
 * Created by Bernat on 27/07/2014.
 */
public class RepoDetailPagerAdapter extends FragmentStatePagerAdapter {
    private String owner;
    private String repo;
    private RefreshListener listener;
    private MarkdownFragment markDownFragment;
    private FilesTreeFragment filesTreeFragment;
    private IssuesFragment issuesFragment;

    public RepoDetailPagerAdapter(FragmentManager fm, String owner, String repo, RefreshListener listener) {
        super(fm);
        this.owner = owner;
        this.repo = repo;
        this.listener = listener;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                if (markDownFragment == null) {
                    markDownFragment = MarkdownFragment.newInstance(owner, repo, listener);
                }
                return markDownFragment;
            case 1:
                if (filesTreeFragment == null) {
                    filesTreeFragment = FilesTreeFragment.newInstance(owner, repo, listener);
                }
                return filesTreeFragment;
            case 2:
                if (issuesFragment == null) {
                    issuesFragment = IssuesFragment.newInstance(owner, repo, listener);
                }
                return issuesFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    public void setCurrentBranch(Branch currentBranch) {
        markDownFragment.setCurrentBranch(currentBranch);
        filesTreeFragment.setCurrentBranch(currentBranch);
    }
}

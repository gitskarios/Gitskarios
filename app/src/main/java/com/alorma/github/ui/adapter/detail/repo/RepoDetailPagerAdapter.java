package com.alorma.github.ui.adapter.detail.repo;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.alorma.github.ui.fragment.detail.repo.FilesTreeFragment;
import com.alorma.github.ui.fragment.detail.repo.MarkdownFragment;
import com.alorma.github.ui.listeners.RefreshListener;

/**
 * Created by Bernat on 27/07/2014.
 */
public class RepoDetailPagerAdapter extends FragmentStatePagerAdapter {
    private String owner;
    private String repo;
    private RefreshListener listener;

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
                return MarkdownFragment.newInstance(owner, repo, listener);
            case 1:
                return FilesTreeFragment.newInstance(owner, repo, listener);
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}

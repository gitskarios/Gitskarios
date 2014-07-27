package com.alorma.github.ui.adapter.repos;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.alorma.github.ui.fragment.repos.RepoFragmentType;
import com.alorma.github.ui.fragment.repos.ReposFragmentFactory;

public class ReposPagerAdapter extends FragmentStatePagerAdapter {

    public ReposPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return ReposFragmentFactory.getFragment(RepoFragmentType.REPO);
            case 1:
                return ReposFragmentFactory.getFragment(RepoFragmentType.WATCHED);
            case 2:
                return ReposFragmentFactory.getFragment(RepoFragmentType.STARRED);
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
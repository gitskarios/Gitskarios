package com.alorma.github.ui.fragment.repos;

import android.app.Fragment;

/**
 * Created by Bernat on 17/07/2014.
 */
public class ReposFragmentFactory {

    public static Fragment getFragment(RepoFragmentType type) {
        if (RepoFragmentType.REPO == type) {
            return ReposFragment.newInstance();
        } else if (RepoFragmentType.STARRED == type) {
            return StarredReposFragment.newInstance();
        } else if (RepoFragmentType.WATCHED == type) {
            return WatchedReposFragment.newInstance();
        } else {
            return ReposFragment.newInstance();
        }
    }

    public static Fragment getFragment(RepoFragmentType type, String username) {
        if (RepoFragmentType.REPO == type) {
            return ReposFragment.newInstance(username);
        } else if (RepoFragmentType.STARRED == type) {
            return StarredReposFragment.newInstance(username);
        } else if (RepoFragmentType.WATCHED == type) {
            return WatchedReposFragment.newInstance(username);
        } else {
            return ReposFragment.newInstance(username);
        }
    }
}

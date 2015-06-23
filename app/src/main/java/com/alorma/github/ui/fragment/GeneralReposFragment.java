package com.alorma.github.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alorma.github.R;
import com.alorma.github.ui.fragment.base.BaseFragment;
import com.alorma.github.ui.fragment.repos.ReposFragment;
import com.alorma.github.ui.fragment.repos.StarredReposFragment;
import com.alorma.github.ui.fragment.repos.WatchedReposFragment;
import com.alorma.github.ui.view.SlidingTabLayout;
import com.alorma.github.utils.AttributesUtils;

/**
 * Created by Bernat on 06/06/2015.
 */
public class GeneralReposFragment extends BaseFragment {

    public static GeneralReposFragment newInstance() {
        return new GeneralReposFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.general_repos_fragment, null, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.tabStrip);
        ViewCompat.setElevation(slidingTabLayout, 4);

        slidingTabLayout.setSelectedIndicatorColors(AttributesUtils.getAccentColor(getActivity()));
        slidingTabLayout.setDividerColors(getResources().getColor(R.color.primary_dark_alpha));
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);

        ReposPagerAdapter reposAdapter = new ReposPagerAdapter(getChildFragmentManager());
        viewPager.setOffscreenPageLimit(reposAdapter.getCount());
        viewPager.setAdapter(reposAdapter);

        slidingTabLayout.setViewPager(viewPager);

    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setTitle(R.string.navigation_general_repositories);
    }

    private class ReposPagerAdapter extends FragmentPagerAdapter {
        public ReposPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                default:
                    return ReposFragment.newInstance();
                case 1:
                    return StarredReposFragment.newInstance();
                case 2:
                    return WatchedReposFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                default:
                    return getString(R.string.navigation_repos);
                case 1:
                    return getString(R.string.navigation_starred_repos);
                case 2:
                    return getString(R.string.navigation_watched_repos);
            }
        }
    }
}

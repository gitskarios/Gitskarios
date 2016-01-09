package com.alorma.github.ui.fragment.repos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
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

        final TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabStrip);
        ViewCompat.setElevation(tabLayout, 4);

        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);

        ReposPagerAdapter reposAdapter = new ReposPagerAdapter(getChildFragmentManager());
        viewPager.setOffscreenPageLimit(reposAdapter.getCount());
        viewPager.setAdapter(reposAdapter);

        if (ViewCompat.isLaidOut(tabLayout)) {
            tabLayout.setupWithViewPager(viewPager);
        } else {
            tabLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    tabLayout.setupWithViewPager(viewPager);

                    tabLayout.removeOnLayoutChangeListener(this);
                }
            });
        }
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
                    return CurrentAccountReposFragment.newInstance();
                case 1:
                    return StarredReposFragment.newInstance();
                case 2:
                    return WatchedReposFragment.newInstance();
                case 3:
                    return MembershipReposFragment.newInstance();
                case 4:
                    return ReposFragmentFromOrgs.newInstance();
            }
        }

        @Override
        public int getCount() {
            return 5;
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
                case 3:
                    return getString(R.string.navigation_member_repos);
                case 4:
                    return getString(R.string.navigation_from_orgs);
            }
        }
    }
}

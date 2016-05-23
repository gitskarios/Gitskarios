package com.alorma.github.ui.fragment.repos;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alorma.github.R;
import com.alorma.github.ui.fragment.base.BaseFragment;
import com.alorma.github.utils.AttributesUtils;

public class GeneralReposFragment extends BaseFragment {

  private Context themedContext;

  public static GeneralReposFragment newInstance() {
    return new GeneralReposFragment();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    themedContext = new ContextThemeWrapper(getActivity(), R.style.AppTheme_Repository);
    if (isDarkTheme()) {
      themedContext = new ContextThemeWrapper(getActivity(), R.style.AppTheme_Dark_Repository);
    }
    LayoutInflater layoutInflater = inflater.cloneInContext(themedContext);
    return layoutInflater.inflate(R.layout.general_repos_fragment, null, false);
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
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
            int oldTop, int oldRight, int oldBottom) {
          tabLayout.setupWithViewPager(viewPager);

          tabLayout.removeOnLayoutChangeListener(this);
        }
      });
    }

    if (getActivity() != null) {
      AppCompatActivity activity = (AppCompatActivity) getActivity();
      ActionBar actionBar = activity.getSupportActionBar();
      if (actionBar != null) {
        int color = AttributesUtils.getPrimaryColor(themedContext);
        int colorDark = AttributesUtils.getPrimaryDarkColor(themedContext);
        int colorAccent = AttributesUtils.getAccentColor(themedContext);

        ColorDrawable colorDrawable = new ColorDrawable(color);

        actionBar.setBackgroundDrawable(colorDrawable);

        tabLayout.setSelectedTabIndicatorColor(colorAccent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          activity.getWindow().setStatusBarColor(colorDark);
        }
      }
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

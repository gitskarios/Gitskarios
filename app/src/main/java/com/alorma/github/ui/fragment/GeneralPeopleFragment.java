package com.alorma.github.ui.fragment;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alorma.github.R;
import com.alorma.github.ui.fragment.base.BaseFragment;
import com.alorma.github.ui.fragment.orgs.OrganizationsFragment;
import com.alorma.github.ui.fragment.users.FollowersFragment;
import com.alorma.github.ui.fragment.users.FollowingFragment;
import com.alorma.tapmoc.BackgroundCompat;
import java.util.ArrayList;
import java.util.List;

public class GeneralPeopleFragment extends BaseFragment {

  public static GeneralPeopleFragment newInstance() {
    return new GeneralPeopleFragment();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.people_activity, null, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    final TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabStrip);

    final ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);

    FollowingFragment followingFragment = FollowingFragment.newInstance(null);
    FollowersFragment followersFragment = FollowersFragment.newInstance(null);
    OrganizationsFragment organizationsFragment = OrganizationsFragment.newInstance(null);

    List<Fragment> listFragments = new ArrayList<>();
    listFragments.add(followingFragment);
    listFragments.add(followersFragment);
    listFragments.add(organizationsFragment);

    NavigationPagerAdapter navigationPagerAdapter =
        new NavigationPagerAdapter(getChildFragmentManager(), listFragments);
    viewPager.setAdapter(navigationPagerAdapter);
    viewPager.setOffscreenPageLimit(navigationPagerAdapter.getCount());
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
        int color = ContextCompat.getColor(activity, R.color.md_deep_orange_A700);
        int colorDark = ContextCompat.getColor(activity, R.color.md_deep_orange_900);
        ColorDrawable colorDrawable = new ColorDrawable(color);

        actionBar.setBackgroundDrawable(colorDrawable);

        BackgroundCompat.setBackground(tabLayout, colorDrawable);
        BackgroundCompat.setBackground(tabLayout, colorDrawable);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          activity.getWindow().setStatusBarColor(colorDark);
        }
      }
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    getActivity().setTitle(R.string.navigation_people);
  }

  private class NavigationPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> listFragments;

    public NavigationPagerAdapter(FragmentManager fm, List<Fragment> listFragments) {
      super(fm);
      this.listFragments = listFragments;
    }

    @Override
    public Fragment getItem(int position) {
      return listFragments.get(position);
    }

    @Override
    public int getCount() {
      return listFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
      switch (position) {
        case 0:
          return getString(R.string.navigation_following);
        case 1:
          return getString(R.string.navigation_followers);
        case 2:
          return getString(R.string.menu_organizations);
      }
      return "";
    }
  }
}

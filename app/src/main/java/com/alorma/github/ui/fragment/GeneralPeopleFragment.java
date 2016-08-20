package com.alorma.github.ui.fragment;

import android.support.annotation.StyleRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import com.alorma.github.R;
import com.alorma.github.ui.fragment.base.BasePagerFragment;
import com.alorma.github.ui.fragment.base.NavigationPagerAdapter;
import com.alorma.github.ui.fragment.orgs.OrganizationsFragment;
import com.alorma.github.ui.fragment.users.FollowersFragment;
import com.alorma.github.ui.fragment.users.FollowingFragment;
import java.util.ArrayList;
import java.util.List;

public class GeneralPeopleFragment extends BasePagerFragment {

  public static GeneralPeopleFragment newInstance() {
    return new GeneralPeopleFragment();
  }

  @StyleRes
  @Override
  protected int getLightTheme() {
    return R.style.AppTheme_People;
  }

  @StyleRes
  @Override
  protected int getDarkTheme() {
    return R.style.AppTheme_Dark_People;
  }

  @Override
  protected PagerAdapter provideAdapter(FragmentManager fm) {
    List<Fragment> listFragments = new ArrayList<>();
    listFragments.add(FollowingFragment.newInstance(null));
    listFragments.add(FollowersFragment.newInstance(null));

    List<String> titles = new ArrayList<>();
    titles.add(getString(R.string.navigation_following));
    titles.add(getString(R.string.navigation_followers));
    return new NavigationPagerAdapter(fm, listFragments, titles);
  }

  @Override
  protected int getTitle() {
    return R.string.navigation_people;
  }
}

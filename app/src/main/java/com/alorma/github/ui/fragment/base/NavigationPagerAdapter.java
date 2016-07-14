package com.alorma.github.ui.fragment.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.security.InvalidParameterException;
import java.util.List;

public class NavigationPagerAdapter extends FragmentPagerAdapter {

  private List<Fragment> listFragments;
  private List<String> titles;

  public NavigationPagerAdapter(FragmentManager fm, List<Fragment> listFragments, List<String> titles) {
    super(fm);
    this.listFragments = listFragments;
    this.titles = titles;

    if (listFragments == null || titles == null || listFragments.size() != titles.size()) {
      throw new InvalidParameterException("Fragments and titles don't have same size");
    }
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
    return titles.get(position);
  }
}
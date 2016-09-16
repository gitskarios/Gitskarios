package com.alorma.github.ui.activity.repo;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.List;

public class NavigationAdapter extends FragmentPagerAdapter {

  private final Context context;
  private List<UiNavigation.UiItem> fragments;

  public NavigationAdapter(FragmentManager fm, Context context, List<UiNavigation.UiItem> fragments) {
    super(fm);
    this.context = context;
    this.fragments = fragments;
  }

  @Override
  public Fragment getItem(int position) {
    return fragments.get(position).getFragment();
  }

  @Override
  public int getCount() {
    return fragments != null ? fragments.size() : 0;
  }

  @Override
  public CharSequence getPageTitle(int position) {
    int title = fragments.get(position).getTitle();
    return context.getString(title);
  }
}
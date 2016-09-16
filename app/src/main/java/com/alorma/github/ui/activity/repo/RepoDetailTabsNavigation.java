package com.alorma.github.ui.activity.repo;

import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import com.alorma.github.R;

public class RepoDetailTabsNavigation extends UiNavigation {

  private ViewPager viewPager;

  @Override
  public void apply(AppCompatActivity activity) {
    TabLayout tabLayout = (TabLayout) activity.findViewById(R.id.tabLayout);
    viewPager = (ViewPager) activity.findViewById(R.id.content);

    NavigationAdapter adapter = new NavigationAdapter(activity.getSupportFragmentManager(), activity, this);

    viewPager.setAdapter(adapter);
    tabLayout.setupWithViewPager(viewPager);
    showTabsIcons(activity, tabLayout);
  }

  @Override
  public int getCurrentItem() {
    return viewPager.getCurrentItem();
  }

  private void showTabsIcons(AppCompatActivity activity, TabLayout tabLayout) {
    for (int i = 0; i < size(); i++) {
      TabLayout.Tab tab = tabLayout.getTabAt(i);
      if (tab != null) {
        int iicon = get(i).getIcon();
        if (iicon != 0) {
          Drawable icon = getPageTitle(activity, iicon);
          tab.setIcon(icon);
        }
      }
    }
  }

  private Drawable getPageTitle(AppCompatActivity activity, @DrawableRes int icon) {
    return ContextCompat.getDrawable(activity, icon);
  }
}

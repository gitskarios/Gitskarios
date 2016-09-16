package com.alorma.github.ui.navigation;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.ColorInt;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import com.alorma.github.R;
import com.alorma.github.utils.AttributesUtils;
import java.util.List;

public class TabsNavigation extends UiNavigation {

  private ViewPager viewPager;

  @Override
  public void apply(AppCompatActivity activity) {
    TabLayout tabLayout = (TabLayout) activity.findViewById(R.id.tabLayout);
    viewPager = (ViewPager) activity.findViewById(R.id.content);

    NavigationAdapter adapter = new NavigationAdapter(activity.getSupportFragmentManager(), activity, this);

    viewPager.setAdapter(adapter);
    tabLayout.setupWithViewPager(viewPager);
    showTabsIcons(tabLayout);

    int iconsColor = AttributesUtils.getAccentColor(tabLayout.getContext());
    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
      @Override
      public void onTabSelected(TabLayout.Tab tab) {
        setTabColor(tab, iconsColor);
      }

      @Override
      public void onTabUnselected(TabLayout.Tab tab) {
        setTabColor(tab, Color.WHITE);
      }

      @Override
      public void onTabReselected(TabLayout.Tab tab) {

      }
    });
  }

  @Override
  public int getCurrentItem() {
    return viewPager.getCurrentItem();
  }

  private void showTabsIcons(TabLayout tabLayout) {

    for (int i = 0; i < size(); i++) {
      TabLayout.Tab tab = tabLayout.getTabAt(i);
      if (tab != null) {
        int icon = get(i).getIcon();
        if (icon != 0) {
          tab.setIcon(icon);
          setTabColor(tab, Color.WHITE);
        }
      }
    }
  }

  private void setTabColor(TabLayout.Tab tab, @ColorInt int color) {
    if (tab.getIcon() != null) {
      tab.getIcon().setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }
  }

  private class NavigationAdapter extends FragmentPagerAdapter {

    private final Context context;
    private List<UiItem> fragments;

    private NavigationAdapter(FragmentManager fm, Context context, List<UiNavigation.UiItem> fragments) {
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
      UiNavigation.UiItem uiItem = fragments.get(position);
      if (uiItem.getIcon() == 0) {
        int title = uiItem.getTitle();
        return context.getString(title);
      } else {
        return "";
      }
    }
  }
}

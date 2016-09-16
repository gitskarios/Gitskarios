package com.alorma.github.ui.navigation;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import java.util.ArrayList;

public abstract class UiNavigation extends ArrayList<UiNavigation.UiItem>{

  public abstract void apply(AppCompatActivity activity);

  public abstract int getCurrentItem();

  public static class UiItem {
    @StringRes int title;
    @DrawableRes int icon;
    Fragment fragment;

    public UiItem(int title, int icon, Fragment fragment) {
      this.title = title;
      this.icon = icon;
      this.fragment = fragment;
    }

    public int getTitle() {
      return title;
    }

    public void setTitle(int title) {
      this.title = title;
    }

    public int getIcon() {
      return icon;
    }

    public void setIcon(int icon) {
      this.icon = icon;
    }

    public Fragment getFragment() {
      return fragment;
    }

    public void setFragment(Fragment fragment) {
      this.fragment = fragment;
    }
  }
}

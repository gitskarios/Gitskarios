package com.alorma.github.ui.activity.base;

import com.alorma.github.R;

public abstract class PeopleThemeActivity extends BackActivity {

  @Override
  protected int getAppLightTheme() {
    return R.style.AppTheme_Profile;
  }

  @Override
  protected int getAppDarkTheme() {
    return R.style.AppTheme_Dark_Profile;
  }
}

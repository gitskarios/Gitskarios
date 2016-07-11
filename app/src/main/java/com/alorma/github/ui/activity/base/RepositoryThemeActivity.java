package com.alorma.github.ui.activity.base;

import com.alorma.github.R;

public abstract class RepositoryThemeActivity extends BackActivity {

  @Override
  protected int getAppLightTheme() {
    return R.style.AppTheme_Repository;
  }

  @Override
  protected int getAppDarkTheme() {
    return R.style.AppTheme_Dark_Repository;
  }
}

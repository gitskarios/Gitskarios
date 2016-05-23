package com.alorma.github.ui.activity.base;

import com.alorma.github.R;

public abstract class PeopleThemeActivity extends BackActivity {

  @Override
  protected void configureTheme(boolean dark) {
    if (dark) {
      setTheme(R.style.AppTheme_Dark_Profile);
    } else {
      setTheme(R.style.AppTheme_Profile);
    }
  }
}

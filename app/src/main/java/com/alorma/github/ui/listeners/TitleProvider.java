package com.alorma.github.ui.listeners;

import android.support.annotation.StringRes;
import com.mikepenz.iconics.typeface.IIcon;

public interface TitleProvider {
  @StringRes
  int getTitle();

  IIcon getTitleIcon();
}

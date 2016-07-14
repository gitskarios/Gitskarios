package com.alorma.github.ui.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.alorma.github.R;
import com.alorma.github.ui.fragment.preference.GitskariosPreferenceFragment;

public class DialogUtils {

  public MaterialDialog.Builder builder(Context context) {
    MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
    if (isDarkTheme(context)) {
      builder.theme(Theme.DARK);
    }
    return builder;
  }

  private boolean isDarkTheme(Context context) {
    try {
      SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
      String pref_theme =
          defaultSharedPreferences.getString(GitskariosPreferenceFragment.PREF_THEME, context.getString(R.string.theme_light));
      return "theme_dark".equalsIgnoreCase(pref_theme);
    } catch (Exception e) {
      return false;
    }
  }
}

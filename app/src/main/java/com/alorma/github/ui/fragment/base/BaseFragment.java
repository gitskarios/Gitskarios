package com.alorma.github.ui.fragment.base;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alorma.github.R;
import com.alorma.github.utils.AttributesUtils;
import com.alorma.github.utils.KeyboardUtils;

public class BaseFragment extends Fragment {
  protected MaterialDialog dialog;
  private Context themedContext;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    themedContext = new ContextThemeWrapper(getActivity(), getTheme());
  }

  @Override
  public void onStart() {
    super.onStart();
    colorize();
  }

  protected void colorize() {
    if (getActivity() != null) {
      AppCompatActivity activity = (AppCompatActivity) getActivity();
      ActionBar actionBar = activity.getSupportActionBar();
      if (actionBar != null) {
        int color = AttributesUtils.getPrimaryColor(themedContext);

        ColorDrawable colorDrawable = new ColorDrawable(color);
        actionBar.setBackgroundDrawable(colorDrawable);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          int colorDark = AttributesUtils.getPrimaryDarkColor(themedContext);
          activity.getWindow().setStatusBarColor(colorDark);
        }
      }
    }
  }

  @Override
  public Context getContext() {
    return themedContext;
  }

  @Override
  public void onPause() {
    super.onPause();
    Activity activity = getActivity();
    if (dialog != null && dialog.isShowing() &&
        getActivity() != null && activity.getWindow() != null) {
      KeyboardUtils.lowerKeyboard(activity);
    }
  }

  @Override
  public LayoutInflater getLayoutInflater(Bundle savedInstanceState) {
    return LayoutInflater.from(themedContext);
  }

  @StyleRes
  private int getTheme() {
    int theme = getLightTheme();
    if (isDarkTheme()) {
      theme = getDarkTheme();
    }
    return theme;
  }

  @StyleRes
  protected int getDarkTheme() {
    return R.style.AppTheme_Dark;
  }

  @StyleRes
  protected int getLightTheme() {
    return R.style.AppTheme;
  }

  protected boolean isDarkTheme() {
    SharedPreferences defaultSharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(getActivity());
    String pref_theme =
        defaultSharedPreferences.getString("pref_theme", getString(R.string.theme_light));
    return "theme_dark".equalsIgnoreCase(pref_theme);
  }
}

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
import com.afollestad.materialdialogs.MaterialDialog;
import com.alorma.github.GitskariosApplication;
import com.alorma.github.R;
import com.alorma.github.injector.component.ApplicationComponent;
import com.alorma.github.log.LogWrapper;
import com.alorma.github.track.Tracker;
import com.alorma.github.utils.AttributesUtils;
import com.alorma.github.utils.KeyboardUtils;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import javax.inject.Inject;

public class BaseFragment extends Fragment {
  protected MaterialDialog dialog;

  @Inject protected Tracker tracker;
  @Inject protected LogWrapper logWrapper;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    injectComponents();


    tracker.trackFragment(this);
    logWrapper.i("LogWrapper", this.getClass().getName());
  }

  private void injectComponents() {
    GitskariosApplication application = (GitskariosApplication) getContext().getApplicationContext();
    ApplicationComponent applicationComponent = application.getApplicationComponent();

    applicationComponent.inject(this);

    injectComponents(applicationComponent);
  }

  protected void injectComponents(ApplicationComponent applicationComponent) {

  }

  @Override
  public void onResume() {
    super.onResume();
    colorize();
  }

  protected void colorize() {
    if (getActivity() != null) {
      AppCompatActivity activity = (AppCompatActivity) getActivity();
      ActionBar actionBar = activity.getSupportActionBar();
      if (actionBar != null) {
        int color = AttributesUtils.getPrimaryColor(getContext());

        ColorDrawable colorDrawable = new ColorDrawable(color);
        actionBar.setBackgroundDrawable(colorDrawable);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          int colorDark = AttributesUtils.getPrimaryDarkColor(getContext());
          activity.getWindow().setStatusBarColor(colorDark);
        }
      }
    }
  }

  @Override
  public Context getContext() {
    return new ContextThemeWrapper(getActivity(), getTheme());
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

  public LayoutInflater getThemedLayoutInflater(LayoutInflater inflater) {
    return inflater.cloneInContext(getContext());
  }

  @StyleRes
  private int getTheme() {
    return isDarkTheme() ? getDarkTheme() : getLightTheme();
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
    try {
      SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
      if (defaultSharedPreferences != null) {
        String pref_theme = defaultSharedPreferences.getString("pref_theme", getString(R.string.theme_light));
        return "theme_dark".equalsIgnoreCase(pref_theme);
      }
      return false;
    } catch (Exception e) {
      if (Fabric.isInitialized()) {
        Crashlytics.logException(e);
      }
      return false;
    }
  }
}

package com.alorma.github.ui.fragment.base;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alorma.github.R;
import com.alorma.github.utils.KeyboardUtils;

public class BaseFragment extends Fragment {
  protected MaterialDialog dialog;

  @Override
  public void onPause() {
    super.onPause();
    Activity activity = getActivity();
    if (dialog != null && dialog.isShowing() &&
        getActivity() != null && activity.getWindow() != null) {
      KeyboardUtils.lowerKeyboard(activity);
    }
  }

  protected boolean isDarkTheme() {
    SharedPreferences defaultSharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(getActivity());
    String pref_theme =
        defaultSharedPreferences.getString("pref_theme", getString(R.string.theme_light));
    return "theme_dark".equalsIgnoreCase(pref_theme);
  }
}

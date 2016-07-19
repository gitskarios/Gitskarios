package com.alorma.github.track;

import android.app.Activity;
import android.support.v4.app.Fragment;
import com.alorma.github.ui.fragment.base.BaseFragment;

public interface Tracker {
  void trackActivity(Activity activity);

  void trackFragment(Fragment fragment);
}

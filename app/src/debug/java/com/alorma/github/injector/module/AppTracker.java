package com.alorma.github.injector.module;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;
import com.alorma.github.track.Tracker;

public class AppTracker implements Tracker {
  @Override
  public void trackActivity(Activity activity) {
    Log.i("Tracker-Activity", activity.getClass().getName());
  }

  @Override
  public void trackFragment(Fragment fragment) {
    Log.i("Tracker-Fragment", fragment.getClass().getName());
  }
}

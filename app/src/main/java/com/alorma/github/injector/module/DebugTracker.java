package com.alorma.github.injector.module;

import android.app.Activity;
import android.support.v4.app.Fragment;
import com.alorma.github.track.Tracker;

public class DebugTracker implements Tracker {
  @Override
  public void trackActivity(Activity activity) {

  }

  @Override
  public void trackFragment(Fragment fragment) {

  }

  @Override
  public void trackEvent(String eventName, String attrName, String attrValue) {

  }

  @Override
  public void trackError(Throwable e) {

  }
}

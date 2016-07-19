package com.alorma.github.injector.module;

import android.app.Activity;
import android.support.v4.app.Fragment;
import com.alorma.github.track.Tracker;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;

public class AnswersTracker implements Tracker {

  private Answers answers;
  private Crashlytics crashlytics;

  public AnswersTracker(Answers answers, Crashlytics crashlytics) {
    this.answers = answers;
    this.crashlytics = crashlytics;
  }

  @Override
  public void trackActivity(Activity activity) {
    answers.logContentView(new ContentViewEvent().putContentType("Activity").putContentName(activity.getClass().getSimpleName()));
  }

  @Override
  public void trackFragment(Fragment fragment) {
    answers.logContentView(new ContentViewEvent().putContentType("Fragment").putContentName(fragment.getClass().getSimpleName()));
  }

  @Override
  public void trackEvent(String eventName, String attrName, String attrValue) {
    answers.logCustom(new CustomEvent(eventName).putCustomAttribute(attrName, attrValue));
  }

  @Override
  public void trackError(Throwable e) {
    crashlytics.logException(e);
  }
}

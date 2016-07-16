package com.alorma.github.notifications;

import android.support.annotation.NonNull;
import com.alorma.github.BuildConfig;
import com.alorma.github.account.GetNotificationsJob;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;

public class FirebaseNotificationsJobManager implements AppJobManager {

  private FirebaseJobDispatcher jobDispatcher;

  public FirebaseNotificationsJobManager(FirebaseJobDispatcher jobDispatcher) {
    this.jobDispatcher = jobDispatcher;
  }

  @Override
  public void enable() {
    Job.Builder jobBuilder = jobDispatcher.newJobBuilder();
    jobBuilder.setTag(getJobTag());
    jobBuilder.setService(GetNotificationsJob.class);
    jobBuilder.setRecurring(true);
    jobBuilder.setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL);
    jobBuilder.setConstraints(Constraint.ON_ANY_NETWORK);
    jobBuilder.setLifetime(Lifetime.FOREVER);
    Job job = jobBuilder.build();

    jobDispatcher.schedule(job);
  }

  @Override
  public void disable() {
    jobDispatcher.cancel(getJobTag());
  }

  @NonNull
  private String getJobTag() {
    return BuildConfig.APPLICATION_ID + "-" + "notifications";
  }
}

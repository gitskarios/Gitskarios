package com.alorma.github.notifications;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;
import com.alorma.github.BuildConfig;

public class JobDispatcherJobManager implements AppJobManager {

  private Context context;

  public JobDispatcherJobManager(Context context) {

    this.context = context;
  }

  @Override
  public void enable() {
    Toast.makeText(context, "Enable notifications", Toast.LENGTH_SHORT).show();
  }

  @Override
  public void disable() {
    Toast.makeText(context, "Disable notifications", Toast.LENGTH_SHORT).show();
  }


  @NonNull
  private String getJobTag() {
    return BuildConfig.APPLICATION_ID + "-" + "notifications";
  }


/*

  private void enableAutomaticNotifications() {
    Job job = jobDispatcher.newJobBuilder()
        .setService(GetNotificationsService.class)
        .setTag(getJobTag())
        .setConstraints(Constraint.ON_ANY_NETWORK)
        .setLifetime(Lifetime.FOREVER)
        .setRecurring(true)
        .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
        .build();

    int result = jobDispatcher.schedule(job);
    if (result != FirebaseJobDispatcher.SCHEDULE_RESULT_SUCCESS) {
      jobScheduleError();
    }
  }
 */
}

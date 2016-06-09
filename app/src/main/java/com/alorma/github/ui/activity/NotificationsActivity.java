package com.alorma.github.ui.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.widget.Switch;
import android.widget.Toast;
import com.alorma.github.BuildConfig;
import com.alorma.github.GitskariosApplication;
import com.alorma.github.R;
import com.alorma.github.account.GetNotificationsService;
import com.alorma.github.injector.component.ApplicationComponent;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.NotificationsFragment;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import javax.inject.Inject;

public class NotificationsActivity extends BackActivity {

  public static Intent launchIntent(Context context) {
    return new Intent(context, NotificationsActivity.class);
  }

  public static Intent launchIntent(Context context, String token) {
    Intent intent = launchIntent(context);
    intent.putExtra(EXTRA_WITH_TOKEN, token);
    return intent;
  }

  @Inject FirebaseJobDispatcher jobDispatcher;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.notifications_activity);

    Switch notificationsSwitch = (Switch) findViewById(R.id.notificationsSwitch);

    if (notificationsSwitch != null) {
      notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
        if (isChecked) {
          buttonView.setText(R.string.notifications_enabled);
          enableAutomaticNotifications();
        } else {
          buttonView.setText(R.string.notifications_disabled);
          disableAutomaticNotifications();
        }
      });
    }

    NotificationsFragment notificationsFragment = NotificationsFragment.newInstance();
    notificationsFragment.setArguments(getIntent().getExtras());
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.replace(R.id.content, notificationsFragment);
    ft.commit();

    if (getToolbar() != null) {
      ViewCompat.setElevation(getToolbar(), 4);
    }

    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.cancelAll();

    injectComponent();
  }

  private void injectComponent() {
    GitskariosApplication application = (GitskariosApplication) getApplication();
    ApplicationComponent component = application.getComponent();
    component.inject(this);
  }

  private void enableAutomaticNotifications() {
    Job job = jobDispatcher.newJobBuilder()
        .setService(GetNotificationsService.class)
        .setTag(getJobTag())
        .setConstraints(Constraint.ON_ANY_NETWORK)
        .setTrigger(Trigger.NOW)
        .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
        .setRecurring(true)
        .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
        .build();

    int result = jobDispatcher.schedule(job);
    if (result != FirebaseJobDispatcher.SCHEDULE_RESULT_SUCCESS) {
      jonScheduleError();
    }
  }

  @NonNull
  private String getJobTag() {
    return BuildConfig.APPLICATION_ID + "-" + "notifications";
  }

  private void jonScheduleError() {
    Toast.makeText(this, R.string.notifications_scheule_error, Toast.LENGTH_SHORT).show();
  }

  private void disableAutomaticNotifications() {
    jobDispatcher.cancel(getJobTag());
  }

  @Override
  protected void configureTheme(boolean dark) {
    if (dark) {
      setTheme(R.style.AppTheme_Dark_Notifications);
    } else {
      setTheme(R.style.AppTheme_Notifications);
    }
  }
}

package com.alorma.github.ui.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.widget.Switch;
import com.alorma.github.GitskariosApplication;
import com.alorma.github.R;
import com.alorma.github.injector.component.ApplicationComponent;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.NotificationsFragment;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
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
        } else {
          buttonView.setText(R.string.notifications_disabled);
        }
        ((GitskariosApplication) getApplication()).setNotificationsEnabled(isChecked);
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


  @Override
  protected int getAppDarkTheme() {
    return R.style.AppTheme_Dark_Notifications;
  }

  @Override
  protected int getAppLightTheme() {
    return R.style.AppTheme_Notifications;
  }
}

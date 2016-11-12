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
import com.alorma.github.injector.component.DaggerNotificationsComponent;
import com.alorma.github.injector.component.NotificationsComponent;
import com.alorma.github.injector.module.NotificationsModule;
import com.alorma.github.notifications.AppNotificationsManager;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.NotificationsFragment;
import javax.inject.Inject;

public class NotificationsActivity extends BackActivity {

  private Switch notificationsSwitch;

  public static Intent launchIntent(Context context) {
    return new Intent(context, NotificationsActivity.class);
  }

  public static Intent launchIntent(Context context, String token) {
    Intent intent = launchIntent(context);
    intent.putExtra(EXTRA_WITH_TOKEN, token);
    return intent;
  }

  @Inject AppNotificationsManager appNotificationsManager;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.notifications_activity);

    injectComponent();

    notificationsSwitch = (Switch) findViewById(R.id.notificationsSwitch);

    if (notificationsSwitch != null) {
      notificationsSwitch.setChecked(appNotificationsManager.areNotificationsEnabled());
      changeSwitchText(appNotificationsManager.areNotificationsEnabled());
      notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
        changeSwitchText(isChecked);
        appNotificationsManager.setNotificationsEnabled(isChecked);
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
  }

  private void changeSwitchText(boolean enabled) {
    if (enabled) {
      notificationsSwitch.setText(R.string.notifications_enabled);
    } else {
      notificationsSwitch.setText(R.string.notifications_disabled);
    }
  }

  private void injectComponent() {
    GitskariosApplication application = (GitskariosApplication) getApplication();
    ApplicationComponent applicationComponent = application.getApplicationComponent();

    NotificationsComponent notificationsComponent = DaggerNotificationsComponent.builder()
        .applicationComponent(applicationComponent)
        .notificationsModule(new NotificationsModule())
        .build();
    notificationsComponent.inject(this);
  }

  @Override
  protected int getAppDarkTheme() {
    return R.style.AppTheme_Dark_Notifications;
  }

  @Override
  protected int getAppLightTheme() {
    return R.style.AppTheme_Notifications;
  }

  @Override
  protected void close(boolean navigateUp) {
    if (navigateUp) {
      Intent intent = new Intent(this, MainActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
      startActivity(intent);
      finish();
    } else {
      finish();
    }
  }
}

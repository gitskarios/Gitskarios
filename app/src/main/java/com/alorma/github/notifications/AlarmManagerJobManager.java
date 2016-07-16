package com.alorma.github.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.alorma.github.account.GetNotificationsService;
import java.util.concurrent.TimeUnit;

public class AlarmManagerJobManager implements AppJobManager {

  private final AlarmManager alarmManager;
  private Context context;

  public AlarmManagerJobManager(Context context) {
    this.context = context;
    this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
  }

  @Override
  public void enable() {
    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), TimeUnit.MINUTES.toMillis(30), getPendingIntent());
  }

  @Override
  public void disable() {
    alarmManager.cancel(getPendingIntent());
  }

  private PendingIntent getPendingIntent() {
    Intent i = new Intent(context, GetNotificationsService.class);
    return PendingIntent.getService(context, 0, i, 0);
  }
}

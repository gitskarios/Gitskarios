package com.alorma.github.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by a557114 on 14/09/2015.
 */
public class NotificationsHelper {

  private static String PREFERENCE_FILE = "NOTIFICATIONS_FILE";
  private static String PREFERENCE_NOT_FIRE_KEY = "NOT_FIRE_KEY";

  public static void addNotFireNotification(Context context, long notificationId) {
    SharedPreferences preferences =
        context.getSharedPreferences(PREFERENCE_FILE, Context.MODE_MULTI_PROCESS);

    SharedPreferences.Editor edit = preferences.edit();

    edit.putBoolean(PREFERENCE_NOT_FIRE_KEY + "_" + notificationId, false);

    if (!edit.commit()) {
      edit.apply();
    }
  }

  public static boolean checkNotFireNotification(Context context, long notificationId) {
    SharedPreferences preferences =
        context.getSharedPreferences(PREFERENCE_FILE, Context.MODE_MULTI_PROCESS);
    return !preferences.contains(PREFERENCE_NOT_FIRE_KEY + "_" + notificationId);
  }
}

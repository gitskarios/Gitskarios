package com.alorma.github.gcm;

import android.content.Context;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.google.firebase.messaging.FirebaseMessaging;

public class GcmTopicsHelper {

  public static void registerInTopic(final Context context, final RepoInfo repoInfo) {
    new Thread(() -> {
      try {
        String topic = "/topics/" + repoInfo.owner + "-" + repoInfo.name;
        FirebaseMessaging.getInstance().subscribeToTopic(topic);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }).start();
  }

  public static void unregisterInTopic(final Context context, final RepoInfo repoInfo) {
    new Thread(() -> {
      try {
        String topic = "/topics/" + repoInfo.owner + "-" + repoInfo.name;
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }).start();
  }
}

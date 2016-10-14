package com.alorma.github.gcm;

import com.alorma.github.sdk.bean.info.RepoInfo;
import com.google.firebase.messaging.FirebaseMessaging;
import rx.schedulers.Schedulers;

public class GcmTopicsHelper {

  public static void registerInTopic(final RepoInfo repoInfo) {
    Schedulers.io().createWorker().schedule(() -> {
      String topic = repoInfo.owner + "-" + repoInfo.name;
      FirebaseMessaging.getInstance().subscribeToTopic(topic);
    });
  }

  public static void unregisterInTopic(final RepoInfo repoInfo) {
    Schedulers.io().createWorker().schedule(() -> {
      String topic = repoInfo.owner + "-" + repoInfo.name;
      FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
    });
  }
}

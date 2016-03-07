package com.alorma.github.gcm;

import android.content.Context;
import com.alorma.github.GitskariosSettings;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.google.android.gms.gcm.GcmPubSub;

public class GcmTopicsHelper {

  public static void registerInTopic(final Context context, final RepoInfo repoInfo) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          String gcmToken = new GitskariosSettings(context).getGCMToken();
          GcmPubSub pubSub = GcmPubSub.getInstance(context);

          pubSub.subscribe(gcmToken, "/topics/" + repoInfo.owner + "-" + repoInfo.name, null);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }).start();
  }
}

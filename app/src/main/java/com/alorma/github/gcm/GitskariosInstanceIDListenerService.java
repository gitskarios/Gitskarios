package com.alorma.github.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.alorma.github.GitskariosSettings;
import com.alorma.github.R;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import java.io.IOException;

public class GitskariosInstanceIDListenerService extends IntentService {
  /**
   * Creates an IntentService.  Invoked by your subclass's constructor.
   *
   */
  public GitskariosInstanceIDListenerService() {
    super("GitskariosInstanceIDListenerService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    try {
      InstanceID instanceID = InstanceID.getInstance(this);
      String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
          GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
      new GitskariosSettings(this).saveGCMToken(token);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

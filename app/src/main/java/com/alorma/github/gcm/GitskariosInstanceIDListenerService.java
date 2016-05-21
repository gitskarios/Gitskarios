package com.alorma.github.gcm;

import com.alorma.github.GitskariosSettings;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class GitskariosInstanceIDListenerService extends FirebaseInstanceIdService {

  /**
   * Called if InstanceID token is updated. This may occur if the security of
   * the previous token had been compromised. This call is initiated by the
   * InstanceID provider.
   */
  // [START refresh_token]
  @Override
  public void onTokenRefresh() {
    String token = FirebaseInstanceId.getInstance().getToken();
    new GitskariosSettings(this).saveGCMToken(token);
  }
  // [END refresh_token]
}
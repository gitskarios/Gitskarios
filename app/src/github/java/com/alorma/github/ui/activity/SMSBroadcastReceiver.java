package com.alorma.github.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SMSBroadcastReceiver extends BroadcastReceiver {

  public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

  @Override
  public void onReceive(Context context, Intent intent) {
    if (SMS_RECEIVED.equals(intent.getAction())) {
      Bundle bundle = intent.getExtras();
      if (bundle != null) {
        Object[] pdus = (Object[]) bundle.get("pdus");
        if (pdus != null) {
          final SmsMessage[] messages = new SmsMessage[pdus.length];
          for (int i = 0; i < pdus.length; i++) {
            messages[i] = SmsMessageCompat.createFromPdu((byte[]) pdus[i], "3gpp");
          }
          if (messages.length > -1) {

          }
        }
      }
    }
  }
}
package com.alorma.github.ui.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.telephony.SmsMessage;

public class SmsMessageCompat {

  private static final SmsMessageCompatImpl IMPL;

  static {
    int version = Build.VERSION.SDK_INT;
    if (version >= Build.VERSION_CODES.M) {
      IMPL = new MarshmallowSmsMessageCompatImpl();
    } else {
      IMPL = new BaseSmsMEssageCompatImpl();
    }
  }

  public static SmsMessage createFromPdu(byte[] pdu, String format) {
    return IMPL.createFromPdu(pdu, format);
  }

  interface SmsMessageCompatImpl {
    SmsMessage createFromPdu(byte[] pdu, String format);
  }

  @TargetApi(Build.VERSION_CODES.M) static class MarshmallowSmsMessageCompatImpl
      implements SmsMessageCompatImpl {
    @Override
    public SmsMessage createFromPdu(byte[] pdu, String format) {
      return SmsMessage.createFromPdu(pdu, format);
    }
  }

  static class BaseSmsMEssageCompatImpl implements SmsMessageCompatImpl {
    @Override
    public SmsMessage createFromPdu(byte[] pdu, String format) {
      return SmsMessage.createFromPdu(pdu);
    }
  }
}

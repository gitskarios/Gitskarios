package com.alorma.github.sdk.bean.dto.request;

import android.os.Parcel;
import android.os.Parcelable;

public class WatchBodyRequest implements Parcelable {
  public static final Parcelable.Creator<WatchBodyRequest> CREATOR =
      new Parcelable.Creator<WatchBodyRequest>() {
        public WatchBodyRequest createFromParcel(Parcel source) {
          return new WatchBodyRequest(source);
        }

        public WatchBodyRequest[] newArray(int size) {
          return new WatchBodyRequest[size];
        }
      };
  public boolean subscribed;
  public boolean ignored;

  public WatchBodyRequest() {
  }

  protected WatchBodyRequest(Parcel in) {
    this.subscribed = in.readByte() != 0;
    this.ignored = in.readByte() != 0;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeByte(subscribed ? (byte) 1 : (byte) 0);
    dest.writeByte(ignored ? (byte) 1 : (byte) 0);
  }
}

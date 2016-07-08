package com.alorma.github.sdk.bean.dto.response.events.payload;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 04/10/2014.
 */
public class WatchedEventPayload extends ActionEventPayload implements Parcelable {
  public static final Creator<WatchedEventPayload> CREATOR = new Creator<WatchedEventPayload>() {
    public WatchedEventPayload createFromParcel(Parcel source) {
      return new WatchedEventPayload(source);
    }

    public WatchedEventPayload[] newArray(int size) {
      return new WatchedEventPayload[size];
    }
  };

  public WatchedEventPayload() {
  }

  protected WatchedEventPayload(Parcel in) {
    super(in);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
  }
}

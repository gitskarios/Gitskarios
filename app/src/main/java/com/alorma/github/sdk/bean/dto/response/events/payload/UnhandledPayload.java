package com.alorma.github.sdk.bean.dto.response.events.payload;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 04/10/2014.
 */
public class UnhandledPayload extends GithubEventPayload implements Parcelable {
  public static final Creator<UnhandledPayload> CREATOR = new Creator<UnhandledPayload>() {
    public UnhandledPayload createFromParcel(Parcel source) {
      return new UnhandledPayload(source);
    }

    public UnhandledPayload[] newArray(int size) {
      return new UnhandledPayload[size];
    }
  };

  public UnhandledPayload() {
  }

  protected UnhandledPayload(Parcel in) {
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

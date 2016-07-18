package com.alorma.github.sdk.bean.dto.response.events.payload;

import android.os.Parcel;
import android.os.Parcelable;

public class GithubEventPayload implements Parcelable {
  public GithubEventPayload() {
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
  }

  protected GithubEventPayload(Parcel in) {
  }

  public static final Creator<GithubEventPayload> CREATOR = new Creator<GithubEventPayload>() {
    @Override
    public GithubEventPayload createFromParcel(Parcel source) {
      return new GithubEventPayload(source);
    }

    @Override
    public GithubEventPayload[] newArray(int size) {
      return new GithubEventPayload[size];
    }
  };
}

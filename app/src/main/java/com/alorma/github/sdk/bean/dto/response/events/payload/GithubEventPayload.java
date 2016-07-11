package com.alorma.github.sdk.bean.dto.response.events.payload;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 04/10/2014.
 */
public class GithubEventPayload implements Parcelable {
  public GithubEventPayload() {
  }

  protected GithubEventPayload(Parcel in) {
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
  }
}

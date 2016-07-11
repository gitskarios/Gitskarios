package com.alorma.github.sdk.bean.dto.response.events.payload;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 05/10/2014.
 */
public class ActionEventPayload extends GithubEventPayload implements Parcelable {
  public String action;

  public ActionEventPayload() {
  }

  protected ActionEventPayload(Parcel in) {
    this.action = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.action);
  }
}

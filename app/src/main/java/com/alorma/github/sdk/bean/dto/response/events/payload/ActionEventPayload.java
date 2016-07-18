package com.alorma.github.sdk.bean.dto.response.events.payload;

import android.os.Parcel;
import android.os.Parcelable;

public class ActionEventPayload extends GithubEventPayload implements Parcelable {
  public String action;

  public ActionEventPayload() {
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeString(this.action);
  }

  protected ActionEventPayload(Parcel in) {
    super(in);
    this.action = in.readString();
  }

  public static final Creator<ActionEventPayload> CREATOR = new Creator<ActionEventPayload>() {
    @Override
    public ActionEventPayload createFromParcel(Parcel source) {
      return new ActionEventPayload(source);
    }

    @Override
    public ActionEventPayload[] newArray(int size) {
      return new ActionEventPayload[size];
    }
  };
}

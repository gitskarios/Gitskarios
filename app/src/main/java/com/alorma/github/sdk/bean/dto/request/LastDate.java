package com.alorma.github.sdk.bean.dto.request;

import android.os.Parcel;
import android.os.Parcelable;

public class LastDate implements Parcelable {
  public static final Parcelable.Creator<LastDate> CREATOR = new Parcelable.Creator<LastDate>() {
    public LastDate createFromParcel(Parcel source) {
      return new LastDate(source);
    }

    public LastDate[] newArray(int size) {
      return new LastDate[size];
    }
  };
  public String last_read_at;

  public LastDate(String last_read_at) {
    this.last_read_at = last_read_at;
  }

  protected LastDate(Parcel in) {
    this.last_read_at = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.last_read_at);
  }
}
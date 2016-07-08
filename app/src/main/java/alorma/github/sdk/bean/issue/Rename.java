package com.alorma.github.sdk.bean.issue;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 08/04/2015.
 */
public class Rename implements Parcelable {
  public static final Creator<Rename> CREATOR = new Creator<Rename>() {
    public Rename createFromParcel(Parcel source) {
      return new Rename(source);
    }

    public Rename[] newArray(int size) {
      return new Rename[size];
    }
  };
  public String from;
  public String to;

  public Rename() {
  }

  protected Rename(Parcel in) {
    this.from = in.readString();
    this.to = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.from);
    dest.writeString(this.to);
  }
}

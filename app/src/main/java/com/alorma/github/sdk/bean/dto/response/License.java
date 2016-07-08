package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 10/08/2015.
 */
public class License implements Parcelable {
  public static final Parcelable.Creator<License> CREATOR = new Parcelable.Creator<License>() {
    public License createFromParcel(Parcel source) {
      return new License(source);
    }

    public License[] newArray(int size) {
      return new License[size];
    }
  };
  public String key;
  public String name;
  public String url;
  public boolean featured;

  public License() {
  }

  protected License(Parcel in) {
    this.key = in.readString();
    this.name = in.readString();
    this.url = in.readString();
    this.featured = in.readByte() != 0;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.key);
    dest.writeString(this.name);
    dest.writeString(this.url);
    dest.writeByte(featured ? (byte) 1 : (byte) 0);
  }
}

package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 13/07/2014.
 */
public class Permissions implements Parcelable {

  public static final Parcelable.Creator<Permissions> CREATOR =
      new Parcelable.Creator<Permissions>() {
        public Permissions createFromParcel(Parcel source) {
          return new Permissions(source);
        }

        public Permissions[] newArray(int size) {
          return new Permissions[size];
        }
      };
  public boolean admin;
  public boolean push;
  public boolean pull;

  public Permissions() {
    this.admin = false;
    this.push = false;
    this.pull = false;
  }

  protected Permissions(Parcel in) {
    this.admin = in.readByte() != 0;
    this.push = in.readByte() != 0;
    this.pull = in.readByte() != 0;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeByte(admin ? (byte) 1 : (byte) 0);
    dest.writeByte(push ? (byte) 1 : (byte) 0);
    dest.writeByte(pull ? (byte) 1 : (byte) 0);
  }
}

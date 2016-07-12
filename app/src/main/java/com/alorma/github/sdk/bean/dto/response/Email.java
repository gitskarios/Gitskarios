package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 12/07/2014.
 */
public class Email implements Parcelable {
  public static final Parcelable.Creator<Email> CREATOR = new Parcelable.Creator<Email>() {
    public Email createFromParcel(Parcel source) {
      return new Email(source);
    }

    public Email[] newArray(int size) {
      return new Email[size];
    }
  };
  public String email;
  public boolean verified;
  public boolean primary;

  public Email() {
  }

  protected Email(Parcel in) {
    this.email = in.readString();
    this.verified = in.readByte() != 0;
    this.primary = in.readByte() != 0;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.email);
    dest.writeByte(verified ? (byte) 1 : (byte) 0);
    dest.writeByte(primary ? (byte) 1 : (byte) 0);
  }
}

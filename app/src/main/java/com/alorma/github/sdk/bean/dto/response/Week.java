package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 20/07/2014.
 */
public class Week implements Parcelable {
  public static final Parcelable.Creator<Week> CREATOR = new Parcelable.Creator<Week>() {
    public Week createFromParcel(Parcel source) {
      return new Week(source);
    }

    public Week[] newArray(int size) {
      return new Week[size];
    }
  };
  public String w;
  public int a;
  public int d;
  public int c;

  public Week() {
  }

  protected Week(Parcel in) {
    this.w = in.readString();
    this.a = in.readInt();
    this.d = in.readInt();
    this.c = in.readInt();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.w);
    dest.writeInt(this.a);
    dest.writeInt(this.d);
    dest.writeInt(this.c);
  }
}

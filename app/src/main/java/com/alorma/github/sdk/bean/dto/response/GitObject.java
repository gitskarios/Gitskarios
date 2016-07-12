package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;

public class GitObject extends ShaUrl implements Parcelable {
  public static final Creator<GitObject> CREATOR = new Creator<GitObject>() {
    public GitObject createFromParcel(Parcel source) {
      return new GitObject(source);
    }

    public GitObject[] newArray(int size) {
      return new GitObject[size];
    }
  };
  public String type;

  public GitObject() {
  }

  protected GitObject(Parcel in) {
    super(in);
    this.type = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeString(this.type);
  }
}

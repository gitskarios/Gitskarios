package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;

public class GitBlob extends ShaUrl implements Parcelable {

  public static final Creator<GitBlob> CREATOR = new Creator<GitBlob>() {
    public GitBlob createFromParcel(Parcel source) {
      return new GitBlob(source);
    }

    public GitBlob[] newArray(int size) {
      return new GitBlob[size];
    }
  };
  public String content;
  public int size;
  public String encoding;

  public GitBlob() {
  }

  protected GitBlob(Parcel in) {
    super(in);
    this.content = in.readString();
    this.size = in.readInt();
    this.encoding = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeString(this.content);
    dest.writeInt(this.size);
    dest.writeString(this.encoding);
  }
}

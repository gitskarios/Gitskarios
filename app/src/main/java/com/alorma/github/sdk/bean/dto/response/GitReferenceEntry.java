package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;

public class GitReferenceEntry extends ShaUrl implements Parcelable {

  public static final Creator<GitReferenceEntry> CREATOR = new Creator<GitReferenceEntry>() {
    public GitReferenceEntry createFromParcel(Parcel source) {
      return new GitReferenceEntry(source);
    }

    public GitReferenceEntry[] newArray(int size) {
      return new GitReferenceEntry[size];
    }
  };
  public String type;

  public GitReferenceEntry() {
  }

  protected GitReferenceEntry(Parcel in) {
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

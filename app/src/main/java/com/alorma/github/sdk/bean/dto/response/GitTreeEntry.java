package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;

public class GitTreeEntry extends ShaUrl implements Parcelable {

  public static final Creator<GitTreeEntry> CREATOR = new Creator<GitTreeEntry>() {
    public GitTreeEntry createFromParcel(Parcel source) {
      return new GitTreeEntry(source);
    }

    public GitTreeEntry[] newArray(int size) {
      return new GitTreeEntry[size];
    }
  };
  public String path;
  public String mode;
  public int size;
  public GitTreeType type;

  public GitTreeEntry() {
  }

  protected GitTreeEntry(Parcel in) {
    super(in);
    this.path = in.readString();
    this.mode = in.readString();
    this.size = in.readInt();
    int tmpType = in.readInt();
    this.type = tmpType == -1 ? null : GitTreeType.values()[tmpType];
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeString(this.path);
    dest.writeString(this.mode);
    dest.writeInt(this.size);
    dest.writeInt(this.type == null ? -1 : this.type.ordinal());
  }
}

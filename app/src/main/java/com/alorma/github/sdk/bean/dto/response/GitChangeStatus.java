package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;

public class GitChangeStatus implements Parcelable {
  public static final Creator<GitChangeStatus> CREATOR = new Creator<GitChangeStatus>() {
    public GitChangeStatus createFromParcel(Parcel source) {
      return new GitChangeStatus(source);
    }

    public GitChangeStatus[] newArray(int size) {
      return new GitChangeStatus[size];
    }
  };
  public int additions;
  public int deletions;
  public int total;
  public int changes;

  public GitChangeStatus() {
  }

  protected GitChangeStatus(Parcel in) {
    this.additions = in.readInt();
    this.deletions = in.readInt();
    this.total = in.readInt();
    this.changes = in.readInt();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.additions);
    dest.writeInt(this.deletions);
    dest.writeInt(this.total);
    dest.writeInt(this.changes);
  }
}
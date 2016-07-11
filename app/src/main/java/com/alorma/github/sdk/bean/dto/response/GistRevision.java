package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Date;

public class GistRevision extends ShaUrl implements Parcelable {

  public static final Creator<GistRevision> CREATOR = new Creator<GistRevision>() {
    public GistRevision createFromParcel(Parcel source) {
      return new GistRevision(source);
    }

    public GistRevision[] newArray(int size) {
      return new GistRevision[size];
    }
  };
  public Date committedAt;
  public GitChangeStatus changeStatus;
  public String version;
  public User user;

  public GistRevision() {
  }

  protected GistRevision(Parcel in) {
    super(in);
    long tmpCommittedAt = in.readLong();
    this.committedAt = tmpCommittedAt == -1 ? null : new Date(tmpCommittedAt);
    this.changeStatus = in.readParcelable(GitChangeStatus.class.getClassLoader());
    this.version = in.readString();
    this.user = in.readParcelable(User.class.getClassLoader());
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeLong(committedAt != null ? committedAt.getTime() : -1);
    dest.writeParcelable(this.changeStatus, 0);
    dest.writeString(this.version);
    dest.writeParcelable(this.user, 0);
  }
}
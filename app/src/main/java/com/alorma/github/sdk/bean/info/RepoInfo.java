package com.alorma.github.sdk.bean.info;

import android.os.Parcel;
import android.os.Parcelable;
import core.repositories.Permissions;

public class RepoInfo implements Parcelable {

  public String owner;
  public String name;
  public String branch;
  public Permissions permissions = new Permissions();

  public RepoInfo() {
  }

  @Override
  public String toString() {
    return owner + "/" + name;
  }

  public RepoInfo toCoreRepoInfo() {
    RepoInfo repoInfo = new RepoInfo();
    repoInfo.branch = branch;
    repoInfo.name = name;
    repoInfo.owner = owner;
    repoInfo.permissions = permissions;
    return repoInfo;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.owner);
    dest.writeString(this.name);
    dest.writeString(this.branch);
    dest.writeParcelable(this.permissions, flags);
  }

  protected RepoInfo(Parcel in) {
    this.owner = in.readString();
    this.name = in.readString();
    this.branch = in.readString();
    this.permissions = in.readParcelable(Permissions.class.getClassLoader());
  }

  public static final Creator<RepoInfo> CREATOR = new Creator<RepoInfo>() {
    @Override
    public RepoInfo createFromParcel(Parcel source) {
      return new RepoInfo(source);
    }

    @Override
    public RepoInfo[] newArray(int size) {
      return new RepoInfo[size];
    }
  };
}
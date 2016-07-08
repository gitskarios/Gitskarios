package com.alorma.github.sdk.bean.info;

import android.os.Parcel;
import android.os.Parcelable;
import com.alorma.github.sdk.bean.dto.response.Permissions;

/**
 * Created by Bernat on 07/09/2014.
 */
public class RepoInfo implements Parcelable {

  public static final Creator<RepoInfo> CREATOR = new Creator<RepoInfo>() {
    public RepoInfo createFromParcel(Parcel source) {
      return new RepoInfo(source);
    }

    public RepoInfo[] newArray(int size) {
      return new RepoInfo[size];
    }
  };
  public String owner;
  public String name;
  public String branch;
  public Permissions permissions = new Permissions();

  public RepoInfo() {
  }

  protected RepoInfo(Parcel in) {
    this.owner = in.readString();
    this.name = in.readString();
    this.branch = in.readString();
    this.permissions = in.readParcelable(Permissions.class.getClassLoader());
  }

  @Override
  public String toString() {
    return owner + "/" + name;
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
    dest.writeParcelable(this.permissions, 0);
  }
}
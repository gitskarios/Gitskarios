package com.alorma.github.sdk.bean.info;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 06/09/2014.
 */
public class ReleaseInfo implements Parcelable {

  public static final Creator<ReleaseInfo> CREATOR = new Creator<ReleaseInfo>() {
    public ReleaseInfo createFromParcel(Parcel source) {
      return new ReleaseInfo(source);
    }

    public ReleaseInfo[] newArray(int size) {
      return new ReleaseInfo[size];
    }
  };
  public RepoInfo repoInfo;
  public int num;

  public ReleaseInfo(RepoInfo repoInfo) {
    this.repoInfo = repoInfo;
  }

  protected ReleaseInfo(Parcel in) {
    this.repoInfo = in.readParcelable(RepoInfo.class.getClassLoader());
    this.num = in.readInt();
  }

  @Override
  public String toString() {
    return repoInfo.toString() + " #" + num;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeParcelable(this.repoInfo, 0);
    dest.writeInt(this.num);
  }
}

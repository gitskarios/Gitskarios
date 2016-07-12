package com.alorma.github.sdk.bean.info;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 24/05/2015.
 */
public class CommitInfo implements Parcelable {

  public static final Creator<CommitInfo> CREATOR = new Creator<CommitInfo>() {
    public CommitInfo createFromParcel(Parcel source) {
      return new CommitInfo(source);
    }

    public CommitInfo[] newArray(int size) {
      return new CommitInfo[size];
    }
  };
  public RepoInfo repoInfo;
  public String sha;

  public CommitInfo() {

  }

  protected CommitInfo(Parcel in) {
    this.repoInfo = in.readParcelable(RepoInfo.class.getClassLoader());
    this.sha = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeParcelable(this.repoInfo, 0);
    dest.writeString(this.sha);
  }
}

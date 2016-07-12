package com.alorma.github.sdk.bean.info;

import android.os.Parcel;
import android.os.Parcelable;

public class FileInfo implements Parcelable {

  public static final Creator<FileInfo> CREATOR = new Creator<FileInfo>() {
    public FileInfo createFromParcel(Parcel source) {
      return new FileInfo(source);
    }

    public FileInfo[] newArray(int size) {
      return new FileInfo[size];
    }
  };
  public RepoInfo repoInfo;
  public String path;
  public String content;
  public String name;
  public String head;

  public FileInfo() {
  }

  protected FileInfo(Parcel in) {
    this.repoInfo = in.readParcelable(RepoInfo.class.getClassLoader());
    this.path = in.readString();
    this.content = in.readString();
    this.name = in.readString();
    this.head = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeParcelable(this.repoInfo, 0);
    dest.writeString(this.path);
    dest.writeString(this.content);
    dest.writeString(this.name);
    dest.writeString(this.head);
  }
}
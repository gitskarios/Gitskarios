package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

public class GitTree extends ShaUrl implements Parcelable {

  public static final Creator<GitTree> CREATOR = new Creator<GitTree>() {
    public GitTree createFromParcel(Parcel source) {
      return new GitTree(source);
    }

    public GitTree[] newArray(int size) {
      return new GitTree[size];
    }
  };
  public List<GitTreeEntry> tree;
  public boolean truncated;

  public GitTree() {
  }

  protected GitTree(Parcel in) {
    super(in);
    this.tree = in.createTypedArrayList(GitTreeEntry.CREATOR);
    this.truncated = in.readByte() != 0;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeTypedList(tree);
    dest.writeByte(truncated ? (byte) 1 : (byte) 0);
  }
}

package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 30/05/2015.
 */
public class CommitComment extends GithubComment implements Parcelable {

  public static final Creator<CommitComment> CREATOR = new Creator<CommitComment>() {
    public CommitComment createFromParcel(Parcel source) {
      return new CommitComment(source);
    }

    public CommitComment[] newArray(int size) {
      return new CommitComment[size];
    }
  };
  public int position;
  public int line;
  public String commit_id;
  public String path;

  public CommitComment() {
  }

  protected CommitComment(Parcel in) {
    this.position = in.readInt();
    this.line = in.readInt();
    this.commit_id = in.readString();
    this.path = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeInt(this.position);
    dest.writeInt(this.line);
    dest.writeString(this.commit_id);
    dest.writeString(this.path);
  }
}

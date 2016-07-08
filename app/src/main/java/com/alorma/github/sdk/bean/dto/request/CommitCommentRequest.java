package com.alorma.github.sdk.bean.dto.request;

import android.os.Parcel;
import android.os.Parcelable;

public class CommitCommentRequest implements Parcelable {

  public static final Parcelable.Creator<CommitCommentRequest> CREATOR =
      new Parcelable.Creator<CommitCommentRequest>() {
        public CommitCommentRequest createFromParcel(Parcel source) {
          return new CommitCommentRequest(source);
        }

        public CommitCommentRequest[] newArray(int size) {
          return new CommitCommentRequest[size];
        }
      };
  public String body;
  public String path;
  public int position;

  public CommitCommentRequest() {
  }

  protected CommitCommentRequest(Parcel in) {
    this.body = in.readString();
    this.path = in.readString();
    this.position = in.readInt();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.body);
    dest.writeString(this.path);
    dest.writeInt(this.position);
  }
}

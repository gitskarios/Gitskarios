package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;

public class ReviewComment implements Parcelable {
  public static final Parcelable.Creator<ReviewComment> CREATOR =
      new Parcelable.Creator<ReviewComment>() {
        public ReviewComment createFromParcel(Parcel source) {
          return new ReviewComment(source);
        }

        public ReviewComment[] newArray(int size) {
          return new ReviewComment[size];
        }
      };
  public String url;
  public int id;
  public String diff_hunk;
  public String path;
  public int position;
  public int original_position;
  public String commit_id;
  public String original_commit_id;
  public User user;
  public String body;
  public String created_at;
  public String updated_at;
  public String html_url;
  public String pull_request_url;

  public ReviewComment() {
  }

  protected ReviewComment(Parcel in) {
    this.url = in.readString();
    this.id = in.readInt();
    this.diff_hunk = in.readString();
    this.path = in.readString();
    this.position = in.readInt();
    this.original_position = in.readInt();
    this.commit_id = in.readString();
    this.original_commit_id = in.readString();
    this.user = in.readParcelable(User.class.getClassLoader());
    this.body = in.readString();
    this.created_at = in.readString();
    this.updated_at = in.readString();
    this.html_url = in.readString();
    this.pull_request_url = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.url);
    dest.writeInt(this.id);
    dest.writeString(this.diff_hunk);
    dest.writeString(this.path);
    dest.writeInt(this.position);
    dest.writeInt(this.original_position);
    dest.writeString(this.commit_id);
    dest.writeString(this.original_commit_id);
    dest.writeParcelable(this.user, 0);
    dest.writeString(this.body);
    dest.writeString(this.created_at);
    dest.writeString(this.updated_at);
    dest.writeString(this.html_url);
    dest.writeString(this.pull_request_url);
  }
}

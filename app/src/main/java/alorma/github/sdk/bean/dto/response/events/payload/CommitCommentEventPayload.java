package com.alorma.github.sdk.bean.dto.response.events.payload;

import android.os.Parcel;
import android.os.Parcelable;
import com.alorma.github.sdk.bean.dto.response.CommitComment;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.bean.dto.response.User;

/**
 * Created by Bernat on 05/10/2014.
 */
public class CommitCommentEventPayload extends ActionEventPayload implements Parcelable {
  public static final Creator<CommitCommentEventPayload> CREATOR =
      new Creator<CommitCommentEventPayload>() {
        public CommitCommentEventPayload createFromParcel(Parcel source) {
          return new CommitCommentEventPayload(source);
        }

        public CommitCommentEventPayload[] newArray(int size) {
          return new CommitCommentEventPayload[size];
        }
      };
  public CommitComment comment;
  public Repo repository;
  public User sender;

  public CommitCommentEventPayload() {
  }

  protected CommitCommentEventPayload(Parcel in) {
    super(in);
    this.comment = in.readParcelable(CommitComment.class.getClassLoader());
    this.repository = in.readParcelable(Repo.class.getClassLoader());
    this.sender = in.readParcelable(User.class.getClassLoader());
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeParcelable(this.comment, 0);
    dest.writeParcelable(this.repository, 0);
    dest.writeParcelable(this.sender, 0);
  }
}

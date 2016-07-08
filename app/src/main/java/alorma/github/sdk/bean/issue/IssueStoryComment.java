package com.alorma.github.sdk.bean.issue;

import android.os.Parcel;
import android.os.Parcelable;
import com.alorma.github.sdk.bean.dto.response.GithubComment;
import com.alorma.github.sdk.bean.dto.response.User;

/**
 * Created by Bernat on 07/04/2015.
 */
public class IssueStoryComment implements IssueStoryDetail, Parcelable {

  public static final Parcelable.Creator<IssueStoryComment> CREATOR =
      new Parcelable.Creator<IssueStoryComment>() {
        public IssueStoryComment createFromParcel(Parcel source) {
          return new IssueStoryComment(source);
        }

        public IssueStoryComment[] newArray(int size) {
          return new IssueStoryComment[size];
        }
      };
  public GithubComment comment;
  public long created_at;

  public IssueStoryComment(GithubComment comment) {

    this.comment = comment;
  }

  protected IssueStoryComment(Parcel in) {
    this.comment = in.readParcelable(GithubComment.class.getClassLoader());
    this.created_at = in.readLong();
  }

  @Override
  public boolean isList() {
    return false;
  }

  @Override
  public String getType() {
    return "commented";
  }

  @Override
  public long createdAt() {
    return created_at;
  }

  @Override
  public User user() {
    return comment.user;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeParcelable(this.comment, 0);
    dest.writeLong(this.created_at);
  }
}

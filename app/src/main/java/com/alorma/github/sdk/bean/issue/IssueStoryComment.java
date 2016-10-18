package com.alorma.github.sdk.bean.issue;

import android.os.Parcel;
import android.os.Parcelable;
import core.GithubComment;
import core.User;

public class IssueStoryComment implements IssueStoryDetail, Parcelable {

  public GithubComment comment;
  public long created_at;

  public IssueStoryComment(GithubComment comment) {

    this.comment = comment;
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
    dest.writeParcelable(this.comment, flags);
    dest.writeLong(this.created_at);
  }

  protected IssueStoryComment(Parcel in) {
    this.comment = in.readParcelable(GithubComment.class.getClassLoader());
    this.created_at = in.readLong();
  }

  public static final Parcelable.Creator<IssueStoryComment> CREATOR = new Parcelable.Creator<IssueStoryComment>() {
    @Override
    public IssueStoryComment createFromParcel(Parcel source) {
      return new IssueStoryComment(source);
    }

    @Override
    public IssueStoryComment[] newArray(int size) {
      return new IssueStoryComment[size];
    }
  };
}

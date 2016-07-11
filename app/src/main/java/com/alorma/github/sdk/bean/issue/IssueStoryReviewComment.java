package com.alorma.github.sdk.bean.issue;

import android.os.Parcel;
import android.os.Parcelable;
import com.alorma.github.sdk.bean.dto.response.ReviewComment;
import com.alorma.github.sdk.bean.dto.response.User;

public class IssueStoryReviewComment implements IssueStoryDetail, Parcelable {
  public static final Parcelable.Creator<IssueStoryReviewComment> CREATOR =
      new Parcelable.Creator<IssueStoryReviewComment>() {
        public IssueStoryReviewComment createFromParcel(Parcel source) {
          return new IssueStoryReviewComment(source);
        }

        public IssueStoryReviewComment[] newArray(int size) {
          return new IssueStoryReviewComment[size];
        }
      };
  public ReviewComment event;
  public long created_at;

  public IssueStoryReviewComment(ReviewComment event) {
    this.event = event;
  }

  protected IssueStoryReviewComment(Parcel in) {
    this.event = in.readParcelable(ReviewComment.class.getClassLoader());
    this.created_at = in.readLong();
  }

  @Override
  public boolean isList() {
    return false;
  }

  @Override
  public String getType() {
    return "review_comment";
  }

  @Override
  public long createdAt() {
    return created_at;
  }

  @Override
  public User user() {
    return event.user;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeParcelable(this.event, 0);
    dest.writeLong(this.created_at);
  }
}

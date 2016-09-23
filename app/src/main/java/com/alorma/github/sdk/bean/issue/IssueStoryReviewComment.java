package com.alorma.github.sdk.bean.issue;

import android.os.Parcel;
import com.alorma.github.sdk.bean.dto.response.ReviewComment;
import core.User;

public class IssueStoryReviewComment implements IssueStoryDetail {
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
}

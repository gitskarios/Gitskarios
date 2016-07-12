package com.alorma.github.sdk.bean.dto.response.events.payload;

import android.os.Parcel;
import android.os.Parcelable;
import com.alorma.github.sdk.bean.dto.response.CommitComment;
import com.alorma.github.sdk.bean.dto.response.PullRequest;

public class PullRequestReviewCommentEventPayload extends ActionEventPayload implements Parcelable {
  public static final Creator<PullRequestReviewCommentEventPayload> CREATOR =
      new Creator<PullRequestReviewCommentEventPayload>() {
        public PullRequestReviewCommentEventPayload createFromParcel(Parcel source) {
          return new PullRequestReviewCommentEventPayload(source);
        }

        public PullRequestReviewCommentEventPayload[] newArray(int size) {
          return new PullRequestReviewCommentEventPayload[size];
        }
      };
  public PullRequest pull_request;
  public CommitComment comment;

  public PullRequestReviewCommentEventPayload() {
  }

  protected PullRequestReviewCommentEventPayload(Parcel in) {
    super(in);
    this.pull_request = in.readParcelable(PullRequest.class.getClassLoader());
    this.comment = in.readParcelable(CommitComment.class.getClassLoader());
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeParcelable(this.pull_request, 0);
    dest.writeParcelable(this.comment, 0);
  }
}

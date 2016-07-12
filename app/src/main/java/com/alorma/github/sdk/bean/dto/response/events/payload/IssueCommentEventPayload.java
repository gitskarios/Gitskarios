package com.alorma.github.sdk.bean.dto.response.events.payload;

import android.os.Parcel;
import android.os.Parcelable;
import com.alorma.github.sdk.bean.dto.response.GithubComment;
import com.alorma.github.sdk.bean.dto.response.Issue;

/**
 * Created by Bernat on 05/10/2014.
 */
public class IssueCommentEventPayload extends ActionEventPayload implements Parcelable {
  public static final Creator<IssueCommentEventPayload> CREATOR =
      new Creator<IssueCommentEventPayload>() {
        public IssueCommentEventPayload createFromParcel(Parcel source) {
          return new IssueCommentEventPayload(source);
        }

        public IssueCommentEventPayload[] newArray(int size) {
          return new IssueCommentEventPayload[size];
        }
      };
  public Issue issue;
  public GithubComment comment;

  public IssueCommentEventPayload() {
  }

  protected IssueCommentEventPayload(Parcel in) {
    super(in);
    this.issue = in.readParcelable(Issue.class.getClassLoader());
    this.comment = in.readParcelable(GithubComment.class.getClassLoader());
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeParcelable(this.issue, 0);
    dest.writeParcelable(this.comment, 0);
  }
}

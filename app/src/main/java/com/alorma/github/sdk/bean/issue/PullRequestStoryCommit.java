package com.alorma.github.sdk.bean.issue;

import android.os.Parcel;
import android.os.Parcelable;
import com.alorma.github.sdk.bean.dto.response.Commit;
import com.alorma.github.sdk.bean.dto.response.User;

public class PullRequestStoryCommit implements IssueStoryDetail, Parcelable {

  public static final Parcelable.Creator<PullRequestStoryCommit> CREATOR =
      new Parcelable.Creator<PullRequestStoryCommit>() {
        public PullRequestStoryCommit createFromParcel(Parcel source) {
          return new PullRequestStoryCommit(source);
        }

        public PullRequestStoryCommit[] newArray(int size) {
          return new PullRequestStoryCommit[size];
        }
      };
  public Commit commit;
  public long created_at;

  public PullRequestStoryCommit(Commit commit) {
    this.commit = commit;
  }

  protected PullRequestStoryCommit(Parcel in) {
    this.commit = in.readParcelable(Commit.class.getClassLoader());
    this.created_at = in.readLong();
  }

  @Override
  public boolean isList() {
    return false;
  }

  @Override
  public String getType() {
    return "committed";
  }

  @Override
  public long createdAt() {
    return created_at;
  }

  @Override
  public User user() {
    return commit.committer;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeParcelable(this.commit, 0);
    dest.writeLong(this.created_at);
  }
}

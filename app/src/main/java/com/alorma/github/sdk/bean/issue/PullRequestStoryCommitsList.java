package com.alorma.github.sdk.bean.issue;

import android.os.Parcel;
import android.os.Parcelable;
import com.alorma.github.sdk.bean.dto.response.Commit;
import com.alorma.github.sdk.bean.dto.response.User;
import java.util.ArrayList;

/**
 * Created by Bernat on 07/04/2015.
 */
public class PullRequestStoryCommitsList extends ArrayList<Commit>
    implements IssueStoryDetail, Parcelable {

  public static final Parcelable.Creator<PullRequestStoryCommitsList> CREATOR =
      new Parcelable.Creator<PullRequestStoryCommitsList>() {
        public PullRequestStoryCommitsList createFromParcel(Parcel source) {
          return new PullRequestStoryCommitsList(source);
        }

        public PullRequestStoryCommitsList[] newArray(int size) {
          return new PullRequestStoryCommitsList[size];
        }
      };
  public long created_at;
  public User user;

  public PullRequestStoryCommitsList() {
  }

  protected PullRequestStoryCommitsList(Parcel in) {
    this.created_at = in.readLong();
    this.user = in.readParcelable(User.class.getClassLoader());
  }

  @Override
  public boolean isList() {
    return true;
  }

  @Override
  public String getType() {
    return "pushed";
  }

  @Override
  public long createdAt() {
    return created_at;
  }

  @Override
  public User user() {
    return user;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(this.created_at);
    dest.writeParcelable(this.user, 0);
  }
}

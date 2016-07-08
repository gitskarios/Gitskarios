package com.alorma.github.sdk.bean.issue;

import android.os.Parcel;
import android.os.Parcelable;
import com.alorma.github.sdk.bean.dto.response.Label;
import com.alorma.github.sdk.bean.dto.response.User;
import java.util.ArrayList;

/**
 * Created by Bernat on 18/07/2015.
 */
public class IssueStoryUnlabelList extends ArrayList<Label>
    implements IssueStoryDetail, Parcelable {

  public static final Parcelable.Creator<IssueStoryUnlabelList> CREATOR =
      new Parcelable.Creator<IssueStoryUnlabelList>() {
        public IssueStoryUnlabelList createFromParcel(Parcel source) {
          return new IssueStoryUnlabelList(source);
        }

        public IssueStoryUnlabelList[] newArray(int size) {
          return new IssueStoryUnlabelList[size];
        }
      };
  public long created_at;
  public User user;

  public IssueStoryUnlabelList() {
  }

  protected IssueStoryUnlabelList(Parcel in) {
    this.created_at = in.readLong();
    this.user = in.readParcelable(User.class.getClassLoader());
  }

  @Override
  public boolean isList() {
    return true;
  }

  @Override
  public String getType() {
    return "unlabeled";
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

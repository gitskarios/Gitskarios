package com.alorma.github.sdk.bean.issue;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Comparator;

/**
 * Created by Bernat on 18/07/2015.
 */
public class IssueStoryComparators implements Parcelable {

  public static final Parcelable.Creator<IssueStoryComparators> CREATOR =
      new Parcelable.Creator<IssueStoryComparators>() {
        public IssueStoryComparators createFromParcel(Parcel source) {
          return new IssueStoryComparators(source);
        }

        public IssueStoryComparators[] newArray(int size) {
          return new IssueStoryComparators[size];
        }
      };
  public static Comparator<IssueStoryDetail> ISSUE_STORY_DETAIL_COMPARATOR =
      new Comparator<IssueStoryDetail>() {
        @Override
        public int compare(IssueStoryDetail lhs, IssueStoryDetail rhs) {
          if (lhs.createdAt() > rhs.createdAt()) {
            return 1;
          } else if (lhs.createdAt() < rhs.createdAt()) {
            return -1;
          }
          return 0;
        }
      };

  public IssueStoryComparators() {
  }

  protected IssueStoryComparators(Parcel in) {
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
  }
}

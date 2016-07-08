package com.alorma.github.sdk.bean.dto.response.search;

import android.os.Parcel;
import android.os.Parcelable;
import com.alorma.github.sdk.bean.dto.response.Issue;
import java.util.List;

/**
 * Created by Bernat on 08/08/2014.
 */
public class IssuesSearch extends SearchBase implements Parcelable {
  public static final Parcelable.Creator<IssuesSearch> CREATOR =
      new Parcelable.Creator<IssuesSearch>() {
        public IssuesSearch createFromParcel(Parcel source) {
          return new IssuesSearch(source);
        }

        public IssuesSearch[] newArray(int size) {
          return new IssuesSearch[size];
        }
      };
  public List<Issue> items;

  public IssuesSearch() {
  }

  protected IssuesSearch(Parcel in) {
    this.items = in.createTypedArrayList(Issue.CREATOR);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeTypedList(items);
  }
}

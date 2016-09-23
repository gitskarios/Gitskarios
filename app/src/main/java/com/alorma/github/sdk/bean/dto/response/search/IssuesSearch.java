package com.alorma.github.sdk.bean.dto.response.search;

import android.os.Parcel;
import android.os.Parcelable;
import com.alorma.github.sdk.bean.dto.response.Issue;
import java.util.List;

public class IssuesSearch extends SearchBase implements Parcelable {
  public List<Issue> items;

  public IssuesSearch() {
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeTypedList(this.items);
  }

  protected IssuesSearch(Parcel in) {
    super(in);
    this.items = in.createTypedArrayList(Issue.CREATOR);
  }

  public static final Creator<IssuesSearch> CREATOR = new Creator<IssuesSearch>() {
    @Override
    public IssuesSearch createFromParcel(Parcel source) {
      return new IssuesSearch(source);
    }

    @Override
    public IssuesSearch[] newArray(int size) {
      return new IssuesSearch[size];
    }
  };
}

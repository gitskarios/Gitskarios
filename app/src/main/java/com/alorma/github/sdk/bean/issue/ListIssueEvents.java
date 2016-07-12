package com.alorma.github.sdk.bean.issue;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

/**
 * Created by Bernat on 07/04/2015.
 */
public class ListIssueEvents extends ArrayList<IssueEvent> implements Parcelable {

  public static final Creator<ListIssueEvents> CREATOR = new Creator<ListIssueEvents>() {
    public ListIssueEvents createFromParcel(Parcel source) {
      return new ListIssueEvents(source);
    }

    public ListIssueEvents[] newArray(int size) {
      return new ListIssueEvents[size];
    }
  };

  public ListIssueEvents() {
  }

  protected ListIssueEvents(Parcel in) {
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
  }
}

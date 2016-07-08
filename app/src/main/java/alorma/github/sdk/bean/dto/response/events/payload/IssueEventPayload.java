package com.alorma.github.sdk.bean.dto.response.events.payload;

import android.os.Parcel;
import android.os.Parcelable;
import com.alorma.github.sdk.bean.dto.response.Issue;

/**
 * Created by Bernat on 28/05/2015.
 */
public class IssueEventPayload extends ActionEventPayload implements Parcelable {
  public static final Creator<IssueEventPayload> CREATOR = new Creator<IssueEventPayload>() {
    public IssueEventPayload createFromParcel(Parcel source) {
      return new IssueEventPayload(source);
    }

    public IssueEventPayload[] newArray(int size) {
      return new IssueEventPayload[size];
    }
  };
  public Issue issue;

  public IssueEventPayload() {
  }

  protected IssueEventPayload(Parcel in) {
    super(in);
    this.issue = in.readParcelable(Issue.class.getClassLoader());
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeParcelable(this.issue, 0);
  }
}

package com.alorma.github.sdk.bean.issue;

import android.os.Parcel;
import android.os.Parcelable;
import com.alorma.github.sdk.bean.dto.response.User;

public class IssueStoryEvent implements IssueStoryDetail, Parcelable {
  public static final Creator<IssueStoryEvent> CREATOR = new Creator<IssueStoryEvent>() {
    public IssueStoryEvent createFromParcel(Parcel source) {
      return new IssueStoryEvent(source);
    }

    public IssueStoryEvent[] newArray(int size) {
      return new IssueStoryEvent[size];
    }
  };
  public IssueEvent event;
  public long created_at;

  public IssueStoryEvent(IssueEvent event) {
    this.event = event;
  }

  protected IssueStoryEvent(Parcel in) {
    this.event = in.readParcelable(IssueEvent.class.getClassLoader());
    this.created_at = in.readLong();
  }

  @Override
  public boolean isList() {
    return false;
  }

  @Override
  public String getType() {
    return event.event;
  }

  @Override
  public long createdAt() {
    return created_at;
  }

  @Override
  public User user() {
    if (event.assigner != null) {
      return event.assigner;
    }
    return event.actor;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeParcelable(this.event, 0);
    dest.writeLong(this.created_at);
  }
}

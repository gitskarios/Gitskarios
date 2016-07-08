package com.alorma.github.sdk.bean.issue;

import android.os.Parcel;
import android.os.Parcelable;
import com.alorma.github.sdk.bean.dto.response.Label;
import com.alorma.github.sdk.bean.dto.response.Milestone;
import com.alorma.github.sdk.bean.dto.response.User;

/**
 * Created by Bernat on 07/04/2015.
 */
public class IssueEvent implements Parcelable {
  public static final Creator<IssueEvent> CREATOR = new Creator<IssueEvent>() {
    public IssueEvent createFromParcel(Parcel source) {
      return new IssueEvent(source);
    }

    public IssueEvent[] newArray(int size) {
      return new IssueEvent[size];
    }
  };
  public int id;
  public String url;
  public User actor;
  public String event;
  public String commit_id;
  public String created_at;
  public Label label;
  public Milestone milestone;
  public User assignee;
  public User assigner;
  public Rename rename;

  public IssueEvent() {
  }

  protected IssueEvent(Parcel in) {
    this.id = in.readInt();
    this.url = in.readString();
    this.actor = in.readParcelable(User.class.getClassLoader());
    this.event = in.readString();
    this.commit_id = in.readString();
    this.created_at = in.readString();
    this.label = in.readParcelable(Label.class.getClassLoader());
    this.milestone = in.readParcelable(Milestone.class.getClassLoader());
    this.assignee = in.readParcelable(User.class.getClassLoader());
    this.assigner = in.readParcelable(User.class.getClassLoader());
    this.rename = in.readParcelable(Rename.class.getClassLoader());
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.id);
    dest.writeString(this.url);
    dest.writeParcelable(this.actor, 0);
    dest.writeString(this.event);
    dest.writeString(this.commit_id);
    dest.writeString(this.created_at);
    dest.writeParcelable(this.label, 0);
    dest.writeParcelable(this.milestone, 0);
    dest.writeParcelable(this.assignee, 0);
    dest.writeParcelable(this.assigner, 0);
    dest.writeParcelable(this.rename, 0);
  }
}

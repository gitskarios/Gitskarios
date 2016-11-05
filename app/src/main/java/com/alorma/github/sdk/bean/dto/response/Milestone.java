package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import com.google.gson.annotations.SerializedName;
import core.User;
import java.util.Date;

public class Milestone extends ShaUrl implements Comparable<Milestone> {
  public String title;
  public int number;
  public MilestoneState state;
  public String description;
  public User creator;
  @SerializedName("open_issues") public int openIssues;
  @SerializedName("closed_issues") public int closedIssues;
  @SerializedName("created_at") public Date createdAt;
  @SerializedName("updated_at") public Date updatedAt;
  @SerializedName("due_on") public String dueOn;

  public Milestone() {
  }

  @Override
  public int compareTo(Milestone another) {
    return title.toLowerCase().compareTo(another.title.toLowerCase());
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeString(this.title);
    dest.writeInt(this.number);
    dest.writeInt(this.state == null ? -1 : this.state.ordinal());
    dest.writeString(this.description);
    dest.writeParcelable(this.creator, flags);
    dest.writeInt(this.openIssues);
    dest.writeInt(this.closedIssues);
    dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
    dest.writeLong(this.updatedAt != null ? this.updatedAt.getTime() : -1);
    dest.writeString(this.dueOn);
  }

  protected Milestone(Parcel in) {
    super(in);
    this.title = in.readString();
    this.number = in.readInt();
    int tmpState = in.readInt();
    this.state = tmpState == -1 ? null : MilestoneState.values()[tmpState];
    this.description = in.readString();
    this.creator = in.readParcelable(User.class.getClassLoader());
    this.openIssues = in.readInt();
    this.closedIssues = in.readInt();
    long tmpCreatedAt = in.readLong();
    this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
    long tmpUpdatedAt = in.readLong();
    this.updatedAt = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
    this.dueOn = in.readString();
  }

  public static final Creator<Milestone> CREATOR = new Creator<Milestone>() {
    @Override
    public Milestone createFromParcel(Parcel source) {
      return new Milestone(source);
    }

    @Override
    public Milestone[] newArray(int size) {
      return new Milestone[size];
    }
  };
}

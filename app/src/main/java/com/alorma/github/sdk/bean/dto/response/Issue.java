package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import com.google.gson.annotations.SerializedName;
import core.GithubComment;
import core.User;
import core.issues.Label;
import core.repositories.Repo;
import java.util.List;

public class Issue extends GithubComment {
  public int number;
  public IssueState state;
  public boolean locked;
  public String title;
  public List<Label> labels;
  public User assignee;
  public List<User> assignees;
  public Milestone milestone;
  public int comments;
  @SerializedName("pull_request") public PullRequest pullRequest;
  @SerializedName("closed_at") public String closedAt;

  public Repo repository;

  public Issue() {

  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeInt(this.number);
    dest.writeInt(this.state == null ? -1 : this.state.ordinal());
    dest.writeByte(this.locked ? (byte) 1 : (byte) 0);
    dest.writeString(this.title);
    dest.writeTypedList(this.labels);
    dest.writeParcelable(this.assignee, flags);
    dest.writeTypedList(this.assignees);
    dest.writeParcelable(this.milestone, flags);
    dest.writeInt(this.comments);
    dest.writeParcelable(this.pullRequest, flags);
    dest.writeString(this.closedAt);
    dest.writeParcelable(this.repository, flags);
  }

  protected Issue(Parcel in) {
    super(in);
    this.number = in.readInt();
    int tmpState = in.readInt();
    this.state = tmpState == -1 ? null : IssueState.values()[tmpState];
    this.locked = in.readByte() != 0;
    this.title = in.readString();
    this.labels = in.createTypedArrayList(Label.CREATOR);
    this.assignee = in.readParcelable(User.class.getClassLoader());
    this.assignees = in.createTypedArrayList(User.CREATOR);
    this.milestone = in.readParcelable(Milestone.class.getClassLoader());
    this.comments = in.readInt();
    this.pullRequest = in.readParcelable(PullRequest.class.getClassLoader());
    this.closedAt = in.readString();
    this.repository = in.readParcelable(Repo.class.getClassLoader());
  }

  public static final Creator<Issue> CREATOR = new Creator<Issue>() {
    @Override
    public Issue createFromParcel(Parcel source) {
      return new Issue(source);
    }

    @Override
    public Issue[] newArray(int size) {
      return new Issue[size];
    }
  };
}

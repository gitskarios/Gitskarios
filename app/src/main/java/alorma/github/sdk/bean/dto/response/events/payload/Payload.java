package com.alorma.github.sdk.bean.dto.response.events.payload;

import android.os.Parcel;
import android.os.Parcelable;
import com.alorma.github.sdk.bean.dto.response.Commit;
import com.alorma.github.sdk.bean.dto.response.CommitComment;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.Organization;
import com.alorma.github.sdk.bean.dto.response.PullRequest;
import com.alorma.github.sdk.bean.dto.response.Release;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.bean.dto.response.Team;
import com.alorma.github.sdk.bean.dto.response.User;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by Bernat on 30/09/2015.
 */
public class Payload implements Parcelable {
  public static final Parcelable.Creator<Payload> CREATOR = new Parcelable.Creator<Payload>() {
    public Payload createFromParcel(Parcel source) {
      return new Payload(source);
    }

    public Payload[] newArray(int size) {
      return new Payload[size];
    }
  };
  public String action;
  public Repo repository;
  public User sender;
  public int number;
  public PullRequest pull_request;
  @SerializedName("public") public boolean is_public;
  public Organization org;
  public String created_at;
  public Issue issue;
  public CommitComment comment;
  public Release release;
  public Team team;
  public long push_id;
  public int size;
  public int distinct_size;
  public String ref_type;
  public String ref;
  public String head;
  public String before;
  public List<Commit> commits;
  public Repo forkee;
  public User member;

  public Payload() {
  }

  protected Payload(Parcel in) {
    this.action = in.readString();
    this.repository = in.readParcelable(Repo.class.getClassLoader());
    this.sender = in.readParcelable(User.class.getClassLoader());
    this.number = in.readInt();
    this.pull_request = in.readParcelable(PullRequest.class.getClassLoader());
    this.is_public = in.readByte() != 0;
    this.org = in.readParcelable(Organization.class.getClassLoader());
    this.created_at = in.readString();
    this.issue = in.readParcelable(Issue.class.getClassLoader());
    this.comment = in.readParcelable(CommitComment.class.getClassLoader());
    this.release = in.readParcelable(Release.class.getClassLoader());
    this.team = in.readParcelable(Team.class.getClassLoader());
    this.push_id = in.readLong();
    this.size = in.readInt();
    this.distinct_size = in.readInt();
    this.ref_type = in.readString();
    this.ref = in.readString();
    this.head = in.readString();
    this.before = in.readString();
    this.commits = in.createTypedArrayList(Commit.CREATOR);
    this.forkee = in.readParcelable(Repo.class.getClassLoader());
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.action);
    dest.writeParcelable(this.repository, 0);
    dest.writeParcelable(this.sender, 0);
    dest.writeInt(this.number);
    dest.writeParcelable(this.pull_request, 0);
    dest.writeByte(is_public ? (byte) 1 : (byte) 0);
    dest.writeParcelable(this.org, 0);
    dest.writeString(this.created_at);
    dest.writeParcelable(this.issue, 0);
    dest.writeParcelable(this.comment, 0);
    dest.writeParcelable(this.release, 0);
    dest.writeParcelable(this.team, 0);
    dest.writeLong(this.push_id);
    dest.writeInt(this.size);
    dest.writeInt(this.distinct_size);
    dest.writeString(this.ref_type);
    dest.writeString(this.ref);
    dest.writeString(this.head);
    dest.writeString(this.before);
    dest.writeTypedList(commits);
    dest.writeParcelable(this.forkee, 0);
  }
}

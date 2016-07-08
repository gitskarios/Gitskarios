package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

public class Commit extends ShaUrl implements Parcelable {

  public static final Creator<Commit> CREATOR = new Creator<Commit>() {
    public Commit createFromParcel(Parcel source) {
      return new Commit(source);
    }

    public Commit[] newArray(int size) {
      return new Commit[size];
    }
  };
  private static final int MAX_COMMIT_LENGHT = 80;
  public GitCommit commit;
  public User author;
  public List<ShaUrl> parents;
  public GitChangeStatus stats;
  public User committer;
  public String message;
  public boolean distinct;
  public GitCommitFiles files;
  public int days;
  public int comment_count;

  public Commit() {
  }

  protected Commit(Parcel in) {
    super(in);
    this.commit = in.readParcelable(GitCommit.class.getClassLoader());
    this.author = in.readParcelable(User.class.getClassLoader());
    this.parents = in.createTypedArrayList(ShaUrl.CREATOR);
    this.stats = in.readParcelable(GitChangeStatus.class.getClassLoader());
    this.committer = in.readParcelable(User.class.getClassLoader());
    this.message = in.readString();
    this.distinct = in.readByte() != 0;
    this.files = in.readParcelable(GitCommitFiles.class.getClassLoader());
    this.days = in.readInt();
    this.comment_count = in.readInt();
  }

  @Override
  public String toString() {
    return "[" + sha + "] " + commit.message;
  }

  public String shortMessage() {
    if (message != null) {
      int start = 0;
      int end = Math.min(MAX_COMMIT_LENGHT, message.length());

      return message.substring(start, end);
    }
    return null;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeParcelable(this.commit, 0);
    dest.writeParcelable(this.author, 0);
    dest.writeTypedList(parents);
    dest.writeParcelable(this.stats, 0);
    dest.writeParcelable(this.committer, 0);
    dest.writeString(this.message);
    dest.writeByte(distinct ? (byte) 1 : (byte) 0);
    dest.writeParcelable(this.files, 0);
    dest.writeInt(this.days);
    dest.writeInt(this.comment_count);
  }

  public boolean isCommitVerified() {
    if (commit != null) {
      GitCommitVerification verification = commit.verification;
      return verification != null && verification.verified;
    }
    return false;
  }
}
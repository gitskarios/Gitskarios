package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

public class GitCommit extends ShaUrl implements Parcelable {

  public static final Creator<GitCommit> CREATOR = new Creator<GitCommit>() {
    public GitCommit createFromParcel(Parcel source) {
      return new GitCommit(source);
    }

    public GitCommit[] newArray(int size) {
      return new GitCommit[size];
    }
  };
  private static final int MAX_COMMIT_LENGHT = 80;
  public User committer;
  public List<ShaUrl> parents;
  public User author;
  public String message;
  public ShaUrl tree;
  public int comment_count;
  public GitCommitVerification verification;

  public GitCommit() {
  }

  protected GitCommit(Parcel in) {
    super(in);
    this.committer = in.readParcelable(User.class.getClassLoader());
    this.parents = in.createTypedArrayList(ShaUrl.CREATOR);
    this.author = in.readParcelable(User.class.getClassLoader());
    this.message = in.readString();
    this.tree = in.readParcelable(ShaUrl.class.getClassLoader());
    this.comment_count = in.readInt();
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
    dest.writeParcelable(this.committer, 0);
    dest.writeTypedList(parents);
    dest.writeParcelable(this.author, 0);
    dest.writeString(this.message);
    dest.writeParcelable(this.tree, 0);
    dest.writeInt(this.comment_count);
  }
}

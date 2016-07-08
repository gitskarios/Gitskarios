package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

/**
 * Created by a557114 on 31/07/2015.
 */
public class CompareCommit implements Parcelable {
  public static final Parcelable.Creator<CompareCommit> CREATOR =
      new Parcelable.Creator<CompareCommit>() {
        public CompareCommit createFromParcel(Parcel source) {
          return new CompareCommit(source);
        }

        public CompareCommit[] newArray(int size) {
          return new CompareCommit[size];
        }
      };
  public String url;
  public String html_url;
  public String permalink_url;
  public String diff_url;
  public String patch_url;
  public Commit base_commit;
  public Commit merge_base_commit;
  public String status;
  public int ahead_by;
  public int behind_by;
  public int total_commits;
  public List<Commit> commits;
  public List<CommitFile> files;

  public CompareCommit() {
  }

  protected CompareCommit(Parcel in) {
    this.url = in.readString();
    this.html_url = in.readString();
    this.permalink_url = in.readString();
    this.diff_url = in.readString();
    this.patch_url = in.readString();
    this.base_commit = in.readParcelable(Commit.class.getClassLoader());
    this.merge_base_commit = in.readParcelable(Commit.class.getClassLoader());
    this.status = in.readString();
    this.ahead_by = in.readInt();
    this.behind_by = in.readInt();
    this.total_commits = in.readInt();
    this.commits = in.createTypedArrayList(Commit.CREATOR);
    this.files = in.createTypedArrayList(CommitFile.CREATOR);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.url);
    dest.writeString(this.html_url);
    dest.writeString(this.permalink_url);
    dest.writeString(this.diff_url);
    dest.writeString(this.patch_url);
    dest.writeParcelable(this.base_commit, 0);
    dest.writeParcelable(this.merge_base_commit, 0);
    dest.writeString(this.status);
    dest.writeInt(this.ahead_by);
    dest.writeInt(this.behind_by);
    dest.writeInt(this.total_commits);
    dest.writeTypedList(commits);
    dest.writeTypedList(files);
  }
}

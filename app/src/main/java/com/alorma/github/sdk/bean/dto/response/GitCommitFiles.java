package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

/**
 * Created by Bernat on 22/12/2014.
 */
public class GitCommitFiles extends ArrayList<CommitFile> implements Parcelable {

  public static final Creator<GitCommitFiles> CREATOR = new Creator<GitCommitFiles>() {
    public GitCommitFiles createFromParcel(Parcel source) {
      return new GitCommitFiles(source);
    }

    public GitCommitFiles[] newArray(int size) {
      return new GitCommitFiles[size];
    }
  };

  public GitCommitFiles() {
  }

  protected GitCommitFiles(Parcel in) {
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
  }
}

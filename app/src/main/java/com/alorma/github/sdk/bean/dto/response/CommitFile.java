package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.File;

/**
 * Created by Bernat on 22/12/2014.
 */
public class CommitFile extends GitChangeStatus implements Parcelable {

  public static final Creator<CommitFile> CREATOR = new Creator<CommitFile>() {
    public CommitFile createFromParcel(Parcel source) {
      return new CommitFile(source);
    }

    public CommitFile[] newArray(int size) {
      return new CommitFile[size];
    }
  };
  public String filename;
  public String status;
  public String raw_url;
  public String blob_url;
  public String patch;
  public String sha;

  public CommitFile() {
  }

  protected CommitFile(Parcel in) {
    super(in);
    this.filename = in.readString();
    this.status = in.readString();
    this.raw_url = in.readString();
    this.blob_url = in.readString();
    this.patch = in.readString();
    this.sha = in.readString();
  }

  public String getFileName() {
    if (filename != null) {
      String[] names = filename.split(File.separator);
      if (names.length > 1) {
        int last = names.length - 1;
        return names[last];
      } else {
        return names[0];
      }
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
    dest.writeString(this.filename);
    dest.writeString(this.status);
    dest.writeString(this.raw_url);
    dest.writeString(this.blob_url);
    dest.writeString(this.patch);
    dest.writeString(this.sha);
  }
}

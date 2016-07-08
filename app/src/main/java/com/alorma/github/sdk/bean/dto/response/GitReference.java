package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;

public class GitReference implements Parcelable {

  public static final Parcelable.Creator<GitReference> CREATOR =
      new Parcelable.Creator<GitReference>() {
        public GitReference createFromParcel(Parcel source) {
          return new GitReference(source);
        }

        public GitReference[] newArray(int size) {
          return new GitReference[size];
        }
      };
  public String ref;
  public String url;
  public GitReferenceEntry object;

  public GitReference() {
  }

  protected GitReference(Parcel in) {
    this.ref = in.readString();
    this.url = in.readString();
    this.object = in.readParcelable(GitReferenceEntry.class.getClassLoader());
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.ref);
    dest.writeString(this.url);
    dest.writeParcelable(this.object, 0);
  }
}

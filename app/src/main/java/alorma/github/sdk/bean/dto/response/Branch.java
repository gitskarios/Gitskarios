package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;

public class Branch implements Parcelable {
  public static final Creator<Branch> CREATOR = new Creator<Branch>() {
    public Branch createFromParcel(Parcel source) {
      return new Branch(source);
    }

    public Branch[] newArray(int size) {
      return new Branch[size];
    }
  };
  public String name;
  public Commit commit;
  public Links _links;

  public Branch() {
  }

  protected Branch(Parcel in) {
    this.name = in.readString();
    this.commit = in.readParcelable(Commit.class.getClassLoader());
    this._links = in.readParcelable(Links.class.getClassLoader());
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.name);
    dest.writeParcelable(this.commit, 0);
    dest.writeParcelable(this._links, 0);
  }
}

package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class UserPlan implements Parcelable {

  public static final Parcelable.Creator<UserPlan> CREATOR = new Parcelable.Creator<UserPlan>() {
    public UserPlan createFromParcel(Parcel source) {
      return new UserPlan(source);
    }

    public UserPlan[] newArray(int size) {
      return new UserPlan[size];
    }
  };
  public long collaborators;
  @SerializedName("private_repos") public long privateRepos;
  public long space;
  public String name;

  public UserPlan() {
  }

  protected UserPlan(Parcel in) {
    this.collaborators = in.readLong();
    this.privateRepos = in.readLong();
    this.space = in.readLong();
    this.name = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(this.collaborators);
    dest.writeLong(this.privateRepos);
    dest.writeLong(this.space);
    dest.writeString(this.name);
  }
}
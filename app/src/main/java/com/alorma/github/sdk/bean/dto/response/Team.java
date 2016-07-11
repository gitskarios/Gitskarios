package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 17/05/2015.
 */
public class Team implements Parcelable {
  public static final Parcelable.Creator<Team> CREATOR = new Parcelable.Creator<Team>() {
    public Team createFromParcel(Parcel source) {
      return new Team(source);
    }

    public Team[] newArray(int size) {
      return new Team[size];
    }
  };
  public int id;
  public String url;
  public String name;
  public String slug;
  public String description;
  public String permission;
  public String members_url;
  public String repositories_url;
  public int members_count;
  public int repos_count;
  public Organization organization;

  public Team() {
  }

  protected Team(Parcel in) {
    this.id = in.readInt();
    this.url = in.readString();
    this.name = in.readString();
    this.slug = in.readString();
    this.description = in.readString();
    this.permission = in.readString();
    this.members_url = in.readString();
    this.repositories_url = in.readString();
    this.members_count = in.readInt();
    this.repos_count = in.readInt();
    this.organization = in.readParcelable(Organization.class.getClassLoader());
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.id);
    dest.writeString(this.url);
    dest.writeString(this.name);
    dest.writeString(this.slug);
    dest.writeString(this.description);
    dest.writeString(this.permission);
    dest.writeString(this.members_url);
    dest.writeString(this.repositories_url);
    dest.writeInt(this.members_count);
    dest.writeInt(this.repos_count);
    dest.writeParcelable(this.organization, 0);
  }
}

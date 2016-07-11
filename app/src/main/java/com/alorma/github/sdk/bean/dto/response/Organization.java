package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Date;

/**
 * Created by Bernat on 04/09/2014.
 */
public class Organization extends ShaUrl implements Parcelable {

  public static final Creator<Organization> CREATOR = new Creator<Organization>() {
    public Organization createFromParcel(Parcel source) {
      return new Organization(source);
    }

    public Organization[] newArray(int size) {
      return new Organization[size];
    }
  };
  public int id;
  public String login;
  public String name;
  public String company;
  public Date created_at;
  public Date updated_at;
  public String avatar_url;
  public String gravatar_id;
  public String blog;
  public String bio;
  public String email;
  public String location;
  public UserType type;
  public boolean site_admin;
  public int public_repos;
  public int public_gists;
  public int followers;
  public int following;

  public Organization() {
    super();
  }

  protected Organization(Parcel in) {
    super(in);
    this.id = in.readInt();
    this.login = in.readString();
    this.name = in.readString();
    this.company = in.readString();
    long tmpCreated_at = in.readLong();
    this.created_at = tmpCreated_at == -1 ? null : new Date(tmpCreated_at);
    long tmpUpdated_at = in.readLong();
    this.updated_at = tmpUpdated_at == -1 ? null : new Date(tmpUpdated_at);
    this.avatar_url = in.readString();
    this.gravatar_id = in.readString();
    this.blog = in.readString();
    this.bio = in.readString();
    this.email = in.readString();
    this.location = in.readString();
    int tmpType = in.readInt();
    this.type = tmpType == -1 ? null : UserType.values()[tmpType];
    this.site_admin = in.readByte() != 0;
    this.public_repos = in.readInt();
    this.public_gists = in.readInt();
    this.followers = in.readInt();
    this.following = in.readInt();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeInt(this.id);
    dest.writeString(this.login);
    dest.writeString(this.name);
    dest.writeString(this.company);
    dest.writeLong(created_at != null ? created_at.getTime() : -1);
    dest.writeLong(updated_at != null ? updated_at.getTime() : -1);
    dest.writeString(this.avatar_url);
    dest.writeString(this.gravatar_id);
    dest.writeString(this.blog);
    dest.writeString(this.bio);
    dest.writeString(this.email);
    dest.writeString(this.location);
    dest.writeInt(this.type == null ? -1 : this.type.ordinal());
    dest.writeByte(site_admin ? (byte) 1 : (byte) 0);
    dest.writeInt(this.public_repos);
    dest.writeInt(this.public_gists);
    dest.writeInt(this.followers);
    dest.writeInt(this.following);
  }
}

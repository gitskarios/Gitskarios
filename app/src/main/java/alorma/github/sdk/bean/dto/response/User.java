package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;

public class User extends Organization implements Parcelable {

  public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
    public User createFromParcel(Parcel source) {
      return new User(source);
    }

    public User[] newArray(int size) {
      return new User[size];
    }
  };
  public boolean hireable;
  public String date;
  public String followers_url;
  public String following_url;
  public String gists_url;
  public String starred_url;
  public String subscriptions_url;
  public String organizations_url;
  public String repos_url;
  public String events_url;
  public String received_events_url;
  public int private_gists;
  public int owned_public_repos;
  public int owned_private_repos;
  public int total_public_repos;
  public int total_private_repos;
  public int collaborators;
  public int disk_usage;
  public UserPlan plan;
  public int organizations;

  public User() {
  }

  protected User(Parcel in) {
    super(in);
    this.hireable = in.readByte() != 0;
    this.date = in.readString();
    this.followers_url = in.readString();
    this.following_url = in.readString();
    this.gists_url = in.readString();
    this.starred_url = in.readString();
    this.subscriptions_url = in.readString();
    this.organizations_url = in.readString();
    this.repos_url = in.readString();
    this.events_url = in.readString();
    this.received_events_url = in.readString();
    this.private_gists = in.readInt();
    this.owned_public_repos = in.readInt();
    this.owned_private_repos = in.readInt();
    this.total_public_repos = in.readInt();
    this.total_private_repos = in.readInt();
    this.collaborators = in.readInt();
    this.disk_usage = in.readInt();
    this.plan = in.readParcelable(UserPlan.class.getClassLoader());
    this.organizations = in.readInt();
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("User{");
    sb.append("id=").append(id);
    sb.append(", login='").append(login).append('\'');
    sb.append(", name='").append(name).append('\'');
    sb.append(", company='").append(company).append('\'');
    sb.append(", created_at=").append(created_at);
    sb.append(", updated_at=").append(updated_at);
    sb.append(", hireable=").append(hireable);
    sb.append(", avatar_url='").append(avatar_url).append('\'');
    sb.append(", gravatar_id='").append(gravatar_id).append('\'');
    sb.append(", blog='").append(blog).append('\'');
    sb.append(", bio='").append(bio).append('\'');
    sb.append(", email='").append(email).append('\'');
    sb.append(", location='").append(location).append('\'');
    sb.append(", html_url='").append(html_url).append('\'');
    sb.append(", followers_url='").append(followers_url).append('\'');
    sb.append(", following_url='").append(following_url).append('\'');
    sb.append(", gists_url='").append(gists_url).append('\'');
    sb.append(", starred_url='").append(starred_url).append('\'');
    sb.append(", subscriptions_url='").append(subscriptions_url).append('\'');
    sb.append(", organizations_url='").append(organizations_url).append('\'');
    sb.append(", repos_url='").append(repos_url).append('\'');
    sb.append(", events_url='").append(events_url).append('\'');
    sb.append(", received_events_url='").append(received_events_url).append('\'');
    sb.append(", type=").append(type);
    sb.append(", site_admin=").append(site_admin);
    sb.append(", public_repos=").append(public_repos);
    sb.append(", public_gists=").append(public_gists);
    sb.append(", owned_public_repos=").append(owned_public_repos);
    sb.append(", total_public_repos=").append(total_public_repos);
    sb.append(", followers=").append(followers);
    sb.append(", following=").append(following);
    sb.append(", contributors=").append(collaborators);
    sb.append(", disk_usage=").append(disk_usage);
    sb.append(", plan=").append(plan);
    sb.append('}');
    return sb.toString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeByte(hireable ? (byte) 1 : (byte) 0);
    dest.writeString(this.date);
    dest.writeString(this.followers_url);
    dest.writeString(this.following_url);
    dest.writeString(this.gists_url);
    dest.writeString(this.starred_url);
    dest.writeString(this.subscriptions_url);
    dest.writeString(this.organizations_url);
    dest.writeString(this.repos_url);
    dest.writeString(this.events_url);
    dest.writeString(this.received_events_url);
    dest.writeInt(this.private_gists);
    dest.writeInt(this.owned_public_repos);
    dest.writeInt(this.owned_private_repos);
    dest.writeInt(this.total_public_repos);
    dest.writeInt(this.total_private_repos);
    dest.writeInt(this.collaborators);
    dest.writeInt(this.disk_usage);
    dest.writeParcelable(this.plan, 0);
    dest.writeInt(this.organizations);
  }
}
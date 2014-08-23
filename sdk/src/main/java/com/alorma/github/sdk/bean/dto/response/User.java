package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class User implements Parcelable {

    public int id;
    public String login;
    public String name;
    public String company;

    public Date created_at;
    public Date updated_at;

    public boolean hireable;
    public String avatar_url;
    public String gravatar_id;
    public String blog;
    public String bio;
    public String email;

    public String location;
    public String html_url;
    public String followers_url;
    public String following_url;
    public String gists_url;
    public String starred_url;
    public String subscriptions_url;
    public String organizations_url;
    public String repos_url;
    public String events_url;

    public String received_events_url;
    public UserType type;

    public boolean site_admin;

    public int public_repos;
    public int public_gists;
    public int owned_public_repos;
    public int total_public_repos;
    public int followers;
    public int following;
    public int collaborators;
    public int disk_usage;

    public UserPlan plan;

    public User() {

    }

    protected User(Parcel in) {
        id = in.readInt();
        login = in.readString();
        name = in.readString();
        company = in.readString();
        long tmpCreated_at = in.readLong();
        created_at = tmpCreated_at != -1 ? new Date(tmpCreated_at) : null;
        long tmpUpdated_at = in.readLong();
        updated_at = tmpUpdated_at != -1 ? new Date(tmpUpdated_at) : null;
        hireable = in.readByte() != 0x00;
        avatar_url = in.readString();
        gravatar_id = in.readString();
        blog = in.readString();
        bio = in.readString();
        email = in.readString();
        location = in.readString();
        html_url = in.readString();
        followers_url = in.readString();
        following_url = in.readString();
        gists_url = in.readString();
        starred_url = in.readString();
        subscriptions_url = in.readString();
        organizations_url = in.readString();
        repos_url = in.readString();
        events_url = in.readString();
        received_events_url = in.readString();
        type = (UserType) in.readValue(UserType.class.getClassLoader());
        site_admin = in.readByte() != 0x00;
        public_repos = in.readInt();
        public_gists = in.readInt();
        owned_public_repos = in.readInt();
        total_public_repos = in.readInt();
        followers = in.readInt();
        following = in.readInt();
        collaborators = in.readInt();
        disk_usage = in.readInt();
        plan = (UserPlan) in.readValue(UserPlan.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(login);
        dest.writeString(name);
        dest.writeString(company);
        dest.writeLong(created_at != null ? created_at.getTime() : -1L);
        dest.writeLong(updated_at != null ? updated_at.getTime() : -1L);
        dest.writeByte((byte) (hireable ? 0x01 : 0x00));
        dest.writeString(avatar_url);
        dest.writeString(gravatar_id);
        dest.writeString(blog);
        dest.writeString(bio);
        dest.writeString(email);
        dest.writeString(location);
        dest.writeString(html_url);
        dest.writeString(followers_url);
        dest.writeString(following_url);
        dest.writeString(gists_url);
        dest.writeString(starred_url);
        dest.writeString(subscriptions_url);
        dest.writeString(organizations_url);
        dest.writeString(repos_url);
        dest.writeString(events_url);
        dest.writeString(received_events_url);
        dest.writeValue(type);
        dest.writeByte((byte) (site_admin ? 0x01 : 0x00));
        dest.writeInt(public_repos);
        dest.writeInt(public_gists);
        dest.writeInt(owned_public_repos);
        dest.writeInt(total_public_repos);
        dest.writeInt(followers);
        dest.writeInt(following);
        dest.writeInt(collaborators);
        dest.writeInt(disk_usage);
        dest.writeValue(plan);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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
}
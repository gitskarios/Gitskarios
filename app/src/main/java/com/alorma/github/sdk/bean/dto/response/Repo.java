package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.List;

public class Repo extends ShaUrl implements Parcelable {

  public static final Creator<Repo> CREATOR = new Creator<Repo>() {
    public Repo createFromParcel(Parcel source) {
      return new Repo(source);
    }

    public Repo[] newArray(int size) {
      return new Repo[size];
    }
  };
  public boolean fork;
  @SerializedName("private") public boolean isPrivate;
  public Date created_at;
  public Date pushed_at;
  public Date updated_at;
  public int forks_count;
  public long id;
  public Repo parent;
  public Repo source;
  public String clone_url;
  public String description;
  public String homepage;
  public String git_url;
  public String language;
  public String default_branch;
  public String mirror_url;
  public String name;
  public String full_name;
  public String ssh_url;
  public String svn_url;
  public User owner;
  public int stargazers_count;
  public int subscribers_count;
  public int network_count;
  public int watchers_count;
  public int size;
  public int open_issues_count;
  public boolean has_issues;
  public boolean has_downloads;
  public boolean has_wiki;
  public Permissions permissions;
  public License license;
  public List<Branch> branches;
  public String archive_url;

  public Repo() {
    super();
  }

  protected Repo(Parcel in) {
    super(in);
    this.fork = in.readByte() != 0;
    this.isPrivate = in.readByte() != 0;
    long tmpCreated_at = in.readLong();
    this.created_at = tmpCreated_at == -1 ? null : new Date(tmpCreated_at);
    long tmpPushed_at = in.readLong();
    this.pushed_at = tmpPushed_at == -1 ? null : new Date(tmpPushed_at);
    long tmpUpdated_at = in.readLong();
    this.updated_at = tmpUpdated_at == -1 ? null : new Date(tmpUpdated_at);
    this.forks_count = in.readInt();
    this.id = in.readLong();
    this.parent = in.readParcelable(Repo.class.getClassLoader());
    this.source = in.readParcelable(Repo.class.getClassLoader());
    this.clone_url = in.readString();
    this.description = in.readString();
    this.homepage = in.readString();
    this.git_url = in.readString();
    this.language = in.readString();
    this.default_branch = in.readString();
    this.mirror_url = in.readString();
    this.name = in.readString();
    this.full_name = in.readString();
    this.ssh_url = in.readString();
    this.svn_url = in.readString();
    this.owner = in.readParcelable(User.class.getClassLoader());
    this.stargazers_count = in.readInt();
    this.subscribers_count = in.readInt();
    this.network_count = in.readInt();
    this.watchers_count = in.readInt();
    this.size = in.readInt();
    this.open_issues_count = in.readInt();
    this.has_issues = in.readByte() != 0;
    this.has_downloads = in.readByte() != 0;
    this.has_wiki = in.readByte() != 0;
    this.permissions = in.readParcelable(Permissions.class.getClassLoader());
    this.license = in.readParcelable(License.class.getClassLoader());
    this.branches = in.createTypedArrayList(Branch.CREATOR);
    this.archive_url = in.readString();
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Repo{");
    sb.append("fork=").append(fork);
    sb.append(", isPrivate=").append(isPrivate);
    sb.append(", created_at=").append(created_at);
    sb.append(", pushed_at=").append(pushed_at);
    sb.append(", updated_at=").append(updated_at);
    sb.append(", forks_count=").append(forks_count);
    sb.append(", id=").append(id);
    sb.append(", parent=").append(parent);
    sb.append(", source=").append(source);
    sb.append(", clone_url='").append(clone_url).append('\'');
    sb.append(", description='").append(description).append('\'');
    sb.append(", homepage='").append(homepage).append('\'');
    sb.append(", git_url='").append(git_url).append('\'');
    sb.append(", html_url='").append(html_url).append('\'');
    sb.append(", language='").append(language).append('\'');
    sb.append(", default_branch='").append(default_branch).append('\'');
    sb.append(", mirror_url='").append(mirror_url).append('\'');
    sb.append(", name='").append(name).append('\'');
    sb.append(", full_name='").append(full_name).append('\'');
    sb.append(", ssh_url='").append(ssh_url).append('\'');
    sb.append(", svn_url='").append(svn_url).append('\'');
    sb.append(", url='").append(url).append('\'');
    sb.append(", owner=").append(owner);
    sb.append(", stargazers_count=").append(stargazers_count);
    sb.append(", subscribers_count=").append(subscribers_count);
    sb.append(", subscribers_count=").append(network_count);
    sb.append(", watchers_count=").append(watchers_count);
    sb.append(", size=").append(size);
    sb.append(", open_issues_count=").append(open_issues_count);
    sb.append(", has_issues=").append(has_issues);
    sb.append(", has_downloads=").append(has_downloads);
    sb.append(", has_wiki=").append(has_wiki);
    sb.append(", archive_url=").append(archive_url);
    sb.append(", permissions=").append(permissions);
    sb.append('}');
    return sb.toString();
  }

  public boolean canPull() {
    return permissions != null && permissions.pull;
  }

  public boolean canPush() {
    return permissions != null && permissions.push;
  }

  public boolean canAdmin() {
    return permissions != null && permissions.admin;
  }

  public RepoInfo toInfo() {
    RepoInfo repoInfo = new RepoInfo();
    repoInfo.owner = owner.login;
    repoInfo.name = name;
    repoInfo.permissions = permissions;
    repoInfo.branch = default_branch;
    return repoInfo;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeByte(fork ? (byte) 1 : (byte) 0);
    dest.writeByte(isPrivate ? (byte) 1 : (byte) 0);
    dest.writeLong(created_at != null ? created_at.getTime() : -1);
    dest.writeLong(pushed_at != null ? pushed_at.getTime() : -1);
    dest.writeLong(updated_at != null ? updated_at.getTime() : -1);
    dest.writeInt(this.forks_count);
    dest.writeLong(this.id);
    dest.writeParcelable(this.parent, 0);
    dest.writeParcelable(this.source, 0);
    dest.writeString(this.clone_url);
    dest.writeString(this.description);
    dest.writeString(this.homepage);
    dest.writeString(this.git_url);
    dest.writeString(this.language);
    dest.writeString(this.default_branch);
    dest.writeString(this.mirror_url);
    dest.writeString(this.name);
    dest.writeString(this.full_name);
    dest.writeString(this.ssh_url);
    dest.writeString(this.svn_url);
    dest.writeParcelable(this.owner, 0);
    dest.writeInt(this.stargazers_count);
    dest.writeInt(this.subscribers_count);
    dest.writeInt(this.network_count);
    dest.writeInt(this.watchers_count);
    dest.writeInt(this.size);
    dest.writeInt(this.open_issues_count);
    dest.writeByte(has_issues ? (byte) 1 : (byte) 0);
    dest.writeByte(has_downloads ? (byte) 1 : (byte) 0);
    dest.writeByte(has_wiki ? (byte) 1 : (byte) 0);
    dest.writeParcelable(this.permissions, 0);
    dest.writeParcelable(this.license, 0);
    dest.writeTypedList(branches);
    dest.writeString(archive_url);
  }
}
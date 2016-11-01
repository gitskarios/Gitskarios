package core.repositories;

import android.os.Parcel;
import android.os.Parcelable;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.google.gson.annotations.SerializedName;
import core.ShaUrl;
import core.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Repo extends ShaUrl implements Parcelable {

  @SerializedName("private") public boolean privateRepo;
  @SerializedName("created_at")  public Date createdAt;
  @SerializedName("pushed_at") public Date pushedAt;
  @SerializedName("updated_at") public Date updatedAt;
  public int forks_count;
  public long id;
  public Repo parent;
  public Repo source;
  @SerializedName("clone_url")
  public String cloneUrl;
  public String description;
  public String homepage;
  @SerializedName("gitUrl")
  public String gitUrl;
  public String language;

  @Deprecated
  @SerializedName("default_branch")
  public String defaultBranch;

  @SerializedName("mirror_url")
  public String mirrorUrl;
  public String name;
  @SerializedName("full_name")
  public String fullName;
  public String ssh_url;
  public String svn_url;
  public User owner;
  @SerializedName("stargazers_count")
  public int stargazersCount;
  @SerializedName("subscribers_count")
  public int subscribersCount;
  @SerializedName("network_count")
  public int networkCount;
  @SerializedName("watchers_count")
  public int watchersCount;
  public int size;
  @SerializedName("open_issues_count")
  public int openIssuesCount;
  @SerializedName("has_issues")
  public boolean hasIssues;
  @SerializedName("has_downloads")
  public boolean hasDownloads;
  @SerializedName("has_wiki")
  public boolean hasWiki;
  public Permissions permissions;
  public License license;
  public List<Branch> branches;
  @SerializedName("archive_url")
  public String archiveUrl;

  private Boolean starred;
  private Boolean watched;

  public Branch defaultBranchObject;

  public Repo() {
    super();
  }

  public boolean isPrivateRepo() {
    return privateRepo;
  }

  public void setPrivateRepo(boolean privateRepo) {
    this.privateRepo = privateRepo;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public Date getPushedAt() {
    return pushedAt;
  }

  public void setPushedAt(Date pushedAt) {
    this.pushedAt = pushedAt;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  public int getForks_count() {
    return forks_count;
  }

  public void setForks_count(int forks_count) {
    this.forks_count = forks_count;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public Repo getParent() {
    return parent;
  }

  public void setParent(Repo parent) {
    this.parent = parent;
  }

  public Repo getSource() {
    return source;
  }

  public void setSource(Repo source) {
    this.source = source;
  }

  public String getCloneUrl() {
    return cloneUrl;
  }

  public void setCloneUrl(String cloneUrl) {
    this.cloneUrl = cloneUrl;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getHomepage() {
    return homepage;
  }

  public void setHomepage(String homepage) {
    this.homepage = homepage;
  }

  public String getGitUrl() {
    return gitUrl;
  }

  public void setGitUrl(String gitUrl) {
    this.gitUrl = gitUrl;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getDefaultBranch() {
    return defaultBranch;
  }

  public Branch getDefaultBranchObject() {
    return defaultBranchObject;
  }

  public void setDefaultBranch(String defaultBranch) {
    this.defaultBranch = defaultBranch;
  }

  public String getMirrorUrl() {
    return mirrorUrl;
  }

  public void setMirrorUrl(String mirrorUrl) {
    this.mirrorUrl = mirrorUrl;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getSsh_url() {
    return ssh_url;
  }

  public void setSsh_url(String ssh_url) {
    this.ssh_url = ssh_url;
  }

  public String getSvn_url() {
    return svn_url;
  }

  public void setSvn_url(String svn_url) {
    this.svn_url = svn_url;
  }

  public User getOwner() {
    return owner;
  }

  public void setOwner(User owner) {
    this.owner = owner;
  }

  public int getStargazersCount() {
    return stargazersCount;
  }

  public void setStargazersCount(int stargazersCount) {
    this.stargazersCount = stargazersCount;
  }

  public int getSubscribersCount() {
    return subscribersCount;
  }

  public void setSubscribersCount(int subscribersCount) {
    this.subscribersCount = subscribersCount;
  }

  public int getNetworkCount() {
    return networkCount;
  }

  public void setNetworkCount(int networkCount) {
    this.networkCount = networkCount;
  }

  public int getWatchersCount() {
    return watchersCount;
  }

  public void setWatchersCount(int watchersCount) {
    this.watchersCount = watchersCount;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public int getOpenIssuesCount() {
    return openIssuesCount;
  }

  public void setOpenIssuesCount(int openIssuesCount) {
    this.openIssuesCount = openIssuesCount;
  }

  public boolean isHasIssues() {
    return hasIssues;
  }

  public void setHasIssues(boolean hasIssues) {
    this.hasIssues = hasIssues;
  }

  public boolean isHasDownloads() {
    return hasDownloads;
  }

  public void setHasDownloads(boolean hasDownloads) {
    this.hasDownloads = hasDownloads;
  }

  public boolean isHasWiki() {
    return hasWiki;
  }

  public void setHasWiki(boolean hasWiki) {
    this.hasWiki = hasWiki;
  }

  public Permissions getPermissions() {
    return permissions;
  }

  public void setPermissions(Permissions permissions) {
    this.permissions = permissions;
  }

  public License getLicense() {
    return license;
  }

  public void setLicense(License license) {
    this.license = license;
  }

  public List<Branch> getBranches() {
    return branches;
  }

  public void setBranches(List<Branch> branches) {
    this.branches = branches;
  }

  public String getArchiveUrl() {
    return archiveUrl;
  }

  public void setArchiveUrl(String archiveUrl) {
    this.archiveUrl = archiveUrl;
  }

  public RepoInfo toInfo() {
    RepoInfo repoInfo = new RepoInfo();
    repoInfo.owner = owner.getLogin();
    repoInfo.name = name;
    repoInfo.branch = defaultBranch;
    return repoInfo;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeByte(this.privateRepo ? (byte) 1 : (byte) 0);
    dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
    dest.writeLong(this.pushedAt != null ? this.pushedAt.getTime() : -1);
    dest.writeLong(this.updatedAt != null ? this.updatedAt.getTime() : -1);
    dest.writeInt(this.forks_count);
    dest.writeLong(this.id);
    dest.writeParcelable(this.parent, flags);
    dest.writeParcelable(this.source, flags);
    dest.writeString(this.cloneUrl);
    dest.writeString(this.description);
    dest.writeString(this.homepage);
    dest.writeString(this.gitUrl);
    dest.writeString(this.language);
    dest.writeString(this.defaultBranch);
    dest.writeString(this.mirrorUrl);
    dest.writeString(this.name);
    dest.writeString(this.fullName);
    dest.writeString(this.ssh_url);
    dest.writeString(this.svn_url);
    dest.writeParcelable(this.owner, flags);
    dest.writeInt(this.stargazersCount);
    dest.writeInt(this.subscribersCount);
    dest.writeInt(this.networkCount);
    dest.writeInt(this.watchersCount);
    dest.writeInt(this.size);
    dest.writeInt(this.openIssuesCount);
    dest.writeByte(this.hasIssues ? (byte) 1 : (byte) 0);
    dest.writeByte(this.hasDownloads ? (byte) 1 : (byte) 0);
    dest.writeByte(this.hasWiki ? (byte) 1 : (byte) 0);
    dest.writeParcelable(this.permissions, flags);
    dest.writeParcelable(this.license, flags);
    dest.writeList(this.branches);
    dest.writeString(this.archiveUrl);
  }

  protected Repo(Parcel in) {
    this.privateRepo = in.readByte() != 0;
    long tmpCreatedAt = in.readLong();
    this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
    long tmpPushedAt = in.readLong();
    this.pushedAt = tmpPushedAt == -1 ? null : new Date(tmpPushedAt);
    long tmpUpdatedAt = in.readLong();
    this.updatedAt = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
    this.forks_count = in.readInt();
    this.id = in.readLong();
    this.parent = in.readParcelable(Repo.class.getClassLoader());
    this.source = in.readParcelable(Repo.class.getClassLoader());
    this.cloneUrl = in.readString();
    this.description = in.readString();
    this.homepage = in.readString();
    this.gitUrl = in.readString();
    this.language = in.readString();
    this.defaultBranch = in.readString();
    this.mirrorUrl = in.readString();
    this.name = in.readString();
    this.fullName = in.readString();
    this.ssh_url = in.readString();
    this.svn_url = in.readString();
    this.owner = in.readParcelable(User.class.getClassLoader());
    this.stargazersCount = in.readInt();
    this.subscribersCount = in.readInt();
    this.networkCount = in.readInt();
    this.watchersCount = in.readInt();
    this.size = in.readInt();
    this.openIssuesCount = in.readInt();
    this.hasIssues = in.readByte() != 0;
    this.hasDownloads = in.readByte() != 0;
    this.hasWiki = in.readByte() != 0;
    this.permissions = in.readParcelable(Permissions.class.getClassLoader());
    this.license = in.readParcelable(License.class.getClassLoader());
    this.branches = new ArrayList<Branch>();
    in.readList(this.branches, Branch.class.getClassLoader());
    this.archiveUrl = in.readString();
  }

  public static final Creator<Repo> CREATOR = new Creator<Repo>() {
    @Override
    public Repo createFromParcel(Parcel source) {
      return new Repo(source);
    }

    @Override
    public Repo[] newArray(int size) {
      return new Repo[size];
    }
  };

  public void setStarred(Boolean starred) {
    this.starred = starred;
  }

  public Boolean isStarred() {
    return starred;
  }

  public void setWatched(Boolean watched) {
    this.watched = watched;
  }

  public Boolean isWatched() {
    return watched;
  }
}
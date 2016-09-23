package core.repositories;

import com.alorma.github.sdk.bean.info.RepoInfo;
import com.google.gson.annotations.SerializedName;
import core.ShaUrl;
import core.User;
import java.util.Date;
import java.util.List;

public class Repo extends ShaUrl {

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
}
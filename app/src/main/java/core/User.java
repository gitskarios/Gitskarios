package core;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class User extends ShaUrl implements Parcelable {

  @SerializedName("name") private String name;
  @SerializedName("login") private String login;
  @SerializedName("id") private Integer id;
  @SerializedName("avatar_url") private String avatar;
  @SerializedName("gravatar_id") private String gravatarId;
  @SerializedName("followers_url") private String followersUrl;
  @SerializedName("following_url") private String followingUrl;
  @SerializedName("gists_url") private String gistsUrl;
  @SerializedName("starred_url") private String starredUrl;
  @SerializedName("subscriptions_url") private String subscriptionsUrl;
  @SerializedName("organizations_url") private String organizationsUrl;
  @SerializedName("repos_url") private String reposUrl;
  @SerializedName("events_url") private String eventsUrl;
  @SerializedName("received_events_url") private String receivedEventsUrl;
  @SerializedName("type") private String type;
  @SerializedName("site_admin") private Boolean siteAdmin;
  private Integer organizationsNum;
  public String date;
  private String email;
  @SerializedName("created_at") private Date createdAt;
  private String company;
  private String location;
  private String blog;
  @SerializedName("public_repos") private int publicRepos;
  @SerializedName("public_gists") private int public_gists;

  public User() {
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getGravatarId() {
    return gravatarId;
  }

  public void setGravatarId(String gravatarId) {
    this.gravatarId = gravatarId;
  }

  public String getFollowersUrl() {
    return followersUrl;
  }

  public void setFollowersUrl(String followersUrl) {
    this.followersUrl = followersUrl;
  }

  public String getFollowingUrl() {
    return followingUrl;
  }

  public void setFollowingUrl(String followingUrl) {
    this.followingUrl = followingUrl;
  }

  public String getGistsUrl() {
    return gistsUrl;
  }

  public void setGistsUrl(String gistsUrl) {
    this.gistsUrl = gistsUrl;
  }

  public String getStarredUrl() {
    return starredUrl;
  }

  public void setStarredUrl(String starredUrl) {
    this.starredUrl = starredUrl;
  }

  public String getSubscriptionsUrl() {
    return subscriptionsUrl;
  }

  public void setSubscriptionsUrl(String subscriptionsUrl) {
    this.subscriptionsUrl = subscriptionsUrl;
  }

  public String getOrganizationsUrl() {
    return organizationsUrl;
  }

  public void setOrganizationsUrl(String organizationsUrl) {
    this.organizationsUrl = organizationsUrl;
  }

  public String getReposUrl() {
    return reposUrl;
  }

  public void setReposUrl(String reposUrl) {
    this.reposUrl = reposUrl;
  }

  public String getEventsUrl() {
    return eventsUrl;
  }

  public void setEventsUrl(String eventsUrl) {
    this.eventsUrl = eventsUrl;
  }

  public String getReceivedEventsUrl() {
    return receivedEventsUrl;
  }

  public void setReceivedEventsUrl(String receivedEventsUrl) {
    this.receivedEventsUrl = receivedEventsUrl;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Boolean isSiteAdmin() {
    return siteAdmin;
  }

  public void setSiteAdmin(Boolean siteAdmin) {
    this.siteAdmin = siteAdmin;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public void setOrganizationsNum(Integer organizationsNum) {
    this.organizationsNum = organizationsNum;
  }

  public Integer getOrganizationsCount() {
    return organizationsNum;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.name);
    dest.writeString(this.login);
    dest.writeValue(this.id);
    dest.writeString(this.avatar);
    dest.writeString(this.gravatarId);
    dest.writeString(this.followersUrl);
    dest.writeString(this.followingUrl);
    dest.writeString(this.gistsUrl);
    dest.writeString(this.starredUrl);
    dest.writeString(this.subscriptionsUrl);
    dest.writeString(this.organizationsUrl);
    dest.writeString(this.reposUrl);
    dest.writeString(this.eventsUrl);
    dest.writeString(this.receivedEventsUrl);
    dest.writeString(this.type);
    dest.writeValue(this.siteAdmin);
    dest.writeValue(this.organizationsNum);
    dest.writeString(this.date);
    dest.writeString(this.email);
    dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
  }

  protected User(Parcel in) {
    this.name = in.readString();
    this.login = in.readString();
    this.id = (Integer) in.readValue(Integer.class.getClassLoader());
    this.avatar = in.readString();
    this.gravatarId = in.readString();
    this.followersUrl = in.readString();
    this.followingUrl = in.readString();
    this.gistsUrl = in.readString();
    this.starredUrl = in.readString();
    this.subscriptionsUrl = in.readString();
    this.organizationsUrl = in.readString();
    this.reposUrl = in.readString();
    this.eventsUrl = in.readString();
    this.receivedEventsUrl = in.readString();
    this.type = in.readString();
    this.siteAdmin = (Boolean) in.readValue(Boolean.class.getClassLoader());
    this.organizationsNum = (Integer) in.readValue(Integer.class.getClassLoader());
    this.date = in.readString();
    this.email = in.readString();
    long tmpCreatedAt = in.readLong();
    this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
  }

  public static final Creator<User> CREATOR = new Creator<User>() {
    @Override
    public User createFromParcel(Parcel source) {
      return new User(source);
    }

    @Override
    public User[] newArray(int size) {
      return new User[size];
    }
  };

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getBlog() {
    return blog;
  }

  public void setBlog(String blog) {
    this.blog = blog;
  }

  public int getPublicRepos() {
    return publicRepos;
  }

  public void setPublicRepos(int publicRepos) {
    this.publicRepos = publicRepos;
  }

  public int getPublicGists() {
    return public_gists;
  }

  public void setPublic_gists(int public_gists) {
    this.public_gists = public_gists;
  }

  public String getDate() {
    return date;
  }
}
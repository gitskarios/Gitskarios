package core;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class User extends ShaUrl implements Serializable {
  public static final String UIL = "";

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

  public Integer getOrganizationsNum() {
    return organizationsNum;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import core.User;
import java.util.List;

public class Gist extends ShaUrl implements Parcelable {
  @SerializedName("public") public boolean isPublic;
  public String created_at;
  public String updated_at;
  public int comments;
  public List<GistRevision> history;
  public GistFilesMap files;
  public String description;
  @SerializedName("git_pull_url") public String gitPullUrl;
  @SerializedName("git_push_url") public String gitPushUrl;
  @SerializedName("forks_url") public String forksUrl;
  public String id;
  public User owner;
  public User user;

  public Gist() {
  }
}
package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Gist extends ShaUrl implements Parcelable {

  public static final Creator<Gist> CREATOR = new Creator<Gist>() {
    public Gist createFromParcel(Parcel source) {
      return new Gist(source);
    }

    public Gist[] newArray(int size) {
      return new Gist[size];
    }
  };
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

  protected Gist(Parcel in) {
    super(in);
    this.isPublic = in.readByte() != 0;
    this.created_at = in.readString();
    this.updated_at = in.readString();
    this.comments = in.readInt();
    this.history = in.createTypedArrayList(GistRevision.CREATOR);
    this.files = in.readParcelable(GistFilesMap.class.getClassLoader());
    this.description = in.readString();
    this.gitPullUrl = in.readString();
    this.gitPushUrl = in.readString();
    this.forksUrl = in.readString();
    this.id = in.readString();
    this.owner = in.readParcelable(User.class.getClassLoader());
    this.user = in.readParcelable(User.class.getClassLoader());
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeByte(isPublic ? (byte) 1 : (byte) 0);
    dest.writeString(this.created_at);
    dest.writeString(this.updated_at);
    dest.writeInt(this.comments);
    dest.writeTypedList(history);
    dest.writeParcelable(this.files, 0);
    dest.writeString(this.description);
    dest.writeString(this.gitPullUrl);
    dest.writeString(this.gitPushUrl);
    dest.writeString(this.forksUrl);
    dest.writeString(this.id);
    dest.writeParcelable(this.owner, 0);
    dest.writeParcelable(this.user, 0);
  }
}
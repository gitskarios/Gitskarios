package com.alorma.github.sdk.bean.dto.request;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Bernat on 13/10/2014.
 */
public class RepoRequestDTO implements Parcelable {

  public static final Parcelable.Creator<RepoRequestDTO> CREATOR =
      new Parcelable.Creator<RepoRequestDTO>() {
        public RepoRequestDTO createFromParcel(Parcel source) {
          return new RepoRequestDTO(source);
        }

        public RepoRequestDTO[] newArray(int size) {
          return new RepoRequestDTO[size];
        }
      };
  public String name;
  public String description;
  public String homepage;
  @SerializedName("private") public boolean isPrivate;
  public boolean has_issues;
  public boolean has_wiki;
  public boolean has_downloads;
  public String default_branch;
  public boolean auto_init;
  public String gitignore_template;
  public String license_template;
  public int team_id;

  public RepoRequestDTO() {
  }

  protected RepoRequestDTO(Parcel in) {
    this.name = in.readString();
    this.description = in.readString();
    this.homepage = in.readString();
    this.isPrivate = in.readByte() != 0;
    this.has_issues = in.readByte() != 0;
    this.has_wiki = in.readByte() != 0;
    this.has_downloads = in.readByte() != 0;
    this.default_branch = in.readString();
    this.auto_init = in.readByte() != 0;
    this.gitignore_template = in.readString();
    this.license_template = in.readString();
    this.team_id = in.readInt();
  }

  public boolean isValid() {
    return !isEmpty(name);
  }

  private boolean isEmpty(String s) {
    return s == null || s.isEmpty();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.name);
    dest.writeString(this.description);
    dest.writeString(this.homepage);
    dest.writeByte(isPrivate ? (byte) 1 : (byte) 0);
    dest.writeByte(has_issues ? (byte) 1 : (byte) 0);
    dest.writeByte(has_wiki ? (byte) 1 : (byte) 0);
    dest.writeByte(has_downloads ? (byte) 1 : (byte) 0);
    dest.writeString(this.default_branch);
    dest.writeByte(auto_init ? (byte) 1 : (byte) 0);
    dest.writeString(this.gitignore_template);
    dest.writeString(this.license_template);
    dest.writeInt(this.team_id);
  }
}

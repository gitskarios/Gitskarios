package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;

public class GithubAuthorization implements Parcelable {
  public static final Parcelable.Creator<GithubAuthorization> CREATOR =
      new Parcelable.Creator<GithubAuthorization>() {
        public GithubAuthorization createFromParcel(Parcel source) {
          return new GithubAuthorization(source);
        }

        public GithubAuthorization[] newArray(int size) {
          return new GithubAuthorization[size];
        }
      };
  public int id;
  public String url;
  public String[] scopes;
  public String token;
  public String token_last_eight;
  public String hashed_token;
  public GithubApp app;
  public String note;
  public String note_url;
  public String updated_at;
  public String created_at;
  public String fingerprint;

  public GithubAuthorization() {
  }

  protected GithubAuthorization(Parcel in) {
    this.id = in.readInt();
    this.url = in.readString();
    this.scopes = in.createStringArray();
    this.token = in.readString();
    this.token_last_eight = in.readString();
    this.hashed_token = in.readString();
    this.app = in.readParcelable(GithubApp.class.getClassLoader());
    this.note = in.readString();
    this.note_url = in.readString();
    this.updated_at = in.readString();
    this.created_at = in.readString();
    this.fingerprint = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.id);
    dest.writeString(this.url);
    dest.writeStringArray(this.scopes);
    dest.writeString(this.token);
    dest.writeString(this.token_last_eight);
    dest.writeString(this.hashed_token);
    dest.writeParcelable(this.app, 0);
    dest.writeString(this.note);
    dest.writeString(this.note_url);
    dest.writeString(this.updated_at);
    dest.writeString(this.created_at);
    dest.writeString(this.fingerprint);
  }

  @Override
  public String toString() {
    return "GithubAuthorization{" +
        "id=" + id +
        ", url='" + url + '\'' +
        ", scopes=" + Arrays.toString(scopes) +
        ", token='" + token + '\'' +
        ", token_last_eight='" + token_last_eight + '\'' +
        ", hashed_token='" + hashed_token + '\'' +
        ", app=" + app +
        ", note='" + note + '\'' +
        ", note_url='" + note_url + '\'' +
        ", updated_at='" + updated_at + '\'' +
        ", created_at='" + created_at + '\'' +
        ", fingerprint='" + fingerprint + '\'' +
        '}';
  }
}

package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;

public class GithubApp implements Parcelable {
  public static final Parcelable.Creator<GithubApp> CREATOR = new Parcelable.Creator<GithubApp>() {
    public GithubApp createFromParcel(Parcel source) {
      return new GithubApp(source);
    }

    public GithubApp[] newArray(int size) {
      return new GithubApp[size];
    }
  };
  public String url;
  public String name;
  public String client_id;

  public GithubApp() {
  }

  protected GithubApp(Parcel in) {
    this.url = in.readString();
    this.name = in.readString();
    this.client_id = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.url);
    dest.writeString(this.name);
    dest.writeString(this.client_id);
  }

  @Override
  public String toString() {
    return "GithubApp{" +
        "url='" + url + '\'' +
        ", name='" + name + '\'' +
        ", client_id='" + client_id + '\'' +
        '}';
  }
}

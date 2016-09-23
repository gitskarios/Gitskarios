package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;

public class ShaUrl implements Parcelable {

  private static final int MAX_SHA_LENGHT = 8;
  public String sha;
  public String url;
  public String html_url;

  public ShaUrl() {
  }

  public static String shortShaStatic(String sha) {
    int start = 0;
    int end = Math.min(MAX_SHA_LENGHT, sha.length());

    return sha.substring(start, end);
  }

  public String shortSha() {
    int start = 0;
    int end = Math.min(MAX_SHA_LENGHT, sha.length());

    return sha.substring(start, end);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.sha);
    dest.writeString(this.url);
    dest.writeString(this.html_url);
  }

  protected ShaUrl(Parcel in) {
    this.sha = in.readString();
    this.url = in.readString();
    this.html_url = in.readString();
  }

  public static final Creator<ShaUrl> CREATOR = new Creator<ShaUrl>() {
    @Override
    public ShaUrl createFromParcel(Parcel source) {
      return new ShaUrl(source);
    }

    @Override
    public ShaUrl[] newArray(int size) {
      return new ShaUrl[size];
    }
  };
}

package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by a557114 on 29/07/2015.
 */
public class ReleaseAsset implements Parcelable {

  public static final Parcelable.Creator<ReleaseAsset> CREATOR =
      new Parcelable.Creator<ReleaseAsset>() {
        public ReleaseAsset createFromParcel(Parcel source) {
          return new ReleaseAsset(source);
        }

        public ReleaseAsset[] newArray(int size) {
          return new ReleaseAsset[size];
        }
      };
  public String url;
  public String browser_download_url;
  public int id;
  public String name;
  public String labnel;
  public String state;
  public String content_type;
  public long size = 0;
  public int download_count;
  public String created_at;
  public String updated_at;
  public User uploader;

  public ReleaseAsset() {
  }

  protected ReleaseAsset(Parcel in) {
    this.url = in.readString();
    this.browser_download_url = in.readString();
    this.id = in.readInt();
    this.name = in.readString();
    this.labnel = in.readString();
    this.state = in.readString();
    this.content_type = in.readString();
    this.size = in.readLong();
    this.download_count = in.readInt();
    this.created_at = in.readString();
    this.updated_at = in.readString();
    this.uploader = in.readParcelable(User.class.getClassLoader());
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.url);
    dest.writeString(this.browser_download_url);
    dest.writeInt(this.id);
    dest.writeString(this.name);
    dest.writeString(this.labnel);
    dest.writeString(this.state);
    dest.writeString(this.content_type);
    dest.writeLong(this.size);
    dest.writeInt(this.download_count);
    dest.writeString(this.created_at);
    dest.writeString(this.updated_at);
    dest.writeParcelable(this.uploader, 0);
  }
}

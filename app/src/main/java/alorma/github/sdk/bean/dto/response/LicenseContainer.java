package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 10/08/2015.
 */
public class LicenseContainer implements Parcelable {
  public static final Parcelable.Creator<LicenseContainer> CREATOR =
      new Parcelable.Creator<LicenseContainer>() {
        public LicenseContainer createFromParcel(Parcel source) {
          return new LicenseContainer(source);
        }

        public LicenseContainer[] newArray(int size) {
          return new LicenseContainer[size];
        }
      };
  public String name;
  public String path;
  public String sha;
  public int size;
  public String url;
  public String html_url;
  public String git_url;
  public String download_url;
  public String type;
  public String content;
  public String encoding;
  public License license;

  public LicenseContainer() {
  }

  protected LicenseContainer(Parcel in) {
    this.name = in.readString();
    this.path = in.readString();
    this.sha = in.readString();
    this.size = in.readInt();
    this.url = in.readString();
    this.html_url = in.readString();
    this.git_url = in.readString();
    this.download_url = in.readString();
    this.type = in.readString();
    this.content = in.readString();
    this.encoding = in.readString();
    this.license = in.readParcelable(License.class.getClassLoader());
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.name);
    dest.writeString(this.path);
    dest.writeString(this.sha);
    dest.writeInt(this.size);
    dest.writeString(this.url);
    dest.writeString(this.html_url);
    dest.writeString(this.git_url);
    dest.writeString(this.download_url);
    dest.writeString(this.type);
    dest.writeString(this.content);
    dest.writeString(this.encoding);
    dest.writeParcelable(this.license, 0);
  }
}

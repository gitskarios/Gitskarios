package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class GistFile implements Parcelable {

  public static final Parcelable.Creator<GistFile> CREATOR = new Parcelable.Creator<GistFile>() {
    public GistFile createFromParcel(Parcel source) {
      return new GistFile(source);
    }

    public GistFile[] newArray(int size) {
      return new GistFile[size];
    }
  };
  public int size;
  public String content;
  public String type;
  public String filename;
  @SerializedName("raw_url") public String rawUrl;
  public boolean truncated;
  public String language;

  public GistFile() {
  }

  protected GistFile(Parcel in) {
    this.size = in.readInt();
    this.content = in.readString();
    this.type = in.readString();
    this.filename = in.readString();
    this.rawUrl = in.readString();
    this.truncated = in.readByte() != 0;
    this.language = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.size);
    dest.writeString(this.content);
    dest.writeString(this.type);
    dest.writeString(this.filename);
    dest.writeString(this.rawUrl);
    dest.writeByte(truncated ? (byte) 1 : (byte) 0);
    dest.writeString(this.language);
  }
}
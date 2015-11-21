package com.alorma.github.emoji;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 08/07/2015.
 */
public class Emoji implements Parcelable {

  @SuppressWarnings("unused") public static final Parcelable.Creator<Emoji> CREATOR = new Parcelable.Creator<Emoji>() {
    @Override
    public Emoji createFromParcel(Parcel in) {
      return new Emoji(in);
    }

    @Override
    public Emoji[] newArray(int size) {
      return new Emoji[size];
    }
  };
  private String key;
  private String value;

  public Emoji(String key, String value) {
    this.key = key;
    this.value = value;
  }

  protected Emoji(Parcel in) {
    key = in.readString();
    value = in.readString();
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(key);
    dest.writeString(value);
  }
}

package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 18/02/2015.
 */
public class NotificationSubject extends ShaUrl implements Parcelable {
  public static final Creator<NotificationSubject> CREATOR = new Creator<NotificationSubject>() {
    public NotificationSubject createFromParcel(Parcel source) {
      return new NotificationSubject(source);
    }

    public NotificationSubject[] newArray(int size) {
      return new NotificationSubject[size];
    }
  };
  public String title;
  public String latest_comment_url;
  public String type;

  public NotificationSubject() {
  }

  protected NotificationSubject(Parcel in) {
    super(in);
    this.title = in.readString();
    this.latest_comment_url = in.readString();
    this.type = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeString(this.title);
    dest.writeString(this.latest_comment_url);
    dest.writeString(this.type);
  }
}

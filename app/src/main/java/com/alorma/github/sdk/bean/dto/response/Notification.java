package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;

public class Notification extends ShaUrl implements Parcelable {
  public static final Creator<Notification> CREATOR = new Creator<Notification>() {
    public Notification createFromParcel(Parcel source) {
      return new Notification(source);
    }

    public Notification[] newArray(int size) {
      return new Notification[size];
    }
  };
  public long id;
  public Repo repository;
  public NotificationSubject subject;
  public String reason;
  public boolean unread;
  public String updated_at;
  public String last_read_at;
  public Long adapter_repo_parent_id;

  public Notification() {
  }

  protected Notification(Parcel in) {
    super(in);
    this.id = in.readLong();
    this.repository = in.readParcelable(Repo.class.getClassLoader());
    this.subject = in.readParcelable(NotificationSubject.class.getClassLoader());
    this.reason = in.readString();
    this.unread = in.readByte() != 0;
    this.updated_at = in.readString();
    this.last_read_at = in.readString();
    this.adapter_repo_parent_id = (Long) in.readValue(Long.class.getClassLoader());
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeLong(this.id);
    dest.writeParcelable(this.repository, 0);
    dest.writeParcelable(this.subject, 0);
    dest.writeString(this.reason);
    dest.writeByte(unread ? (byte) 1 : (byte) 0);
    dest.writeString(this.updated_at);
    dest.writeString(this.last_read_at);
    dest.writeValue(this.adapter_repo_parent_id);
  }
}

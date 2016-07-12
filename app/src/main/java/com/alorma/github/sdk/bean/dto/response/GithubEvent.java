package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;
import com.alorma.github.sdk.bean.dto.response.events.EventType;
import com.alorma.github.sdk.bean.dto.response.events.payload.Payload;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Bernat on 31/08/2014.
 */
public class GithubEvent implements Parcelable {
  public static final Creator<GithubEvent> CREATOR = new Creator<GithubEvent>() {
    public GithubEvent createFromParcel(Parcel source) {
      return new GithubEvent(source);
    }

    public GithubEvent[] newArray(int size) {
      return new GithubEvent[size];
    }
  };
  public long id;
  public EventType type = EventType.Unhandled;
  public String name;
  public User actor;
  public User org;
  public Repo repo;
  public Payload payload;
  @SerializedName("public") public boolean public_event;
  public String created_at;

  public GithubEvent() {
  }

  protected GithubEvent(Parcel in) {
    this.id = in.readLong();
    int tmpType = in.readInt();
    this.type = tmpType == -1 ? null : EventType.values()[tmpType];
    this.name = in.readString();
    this.actor = in.readParcelable(User.class.getClassLoader());
    this.org = in.readParcelable(User.class.getClassLoader());
    this.repo = in.readParcelable(Repo.class.getClassLoader());
    this.payload = in.readParcelable(Payload.class.getClassLoader());
    this.public_event = in.readByte() != 0;
    this.created_at = in.readString();
  }

  public EventType getType() {
    return type != null ? type : EventType.Unhandled;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(this.id);
    dest.writeInt(this.type == null ? -1 : this.type.ordinal());
    dest.writeString(this.name);
    dest.writeParcelable(this.actor, 0);
    dest.writeParcelable(this.org, 0);
    dest.writeParcelable(this.repo, 0);
    dest.writeParcelable(this.payload, 0);
    dest.writeByte(public_event ? (byte) 1 : (byte) 0);
    dest.writeString(this.created_at);
  }
}

package com.alorma.github.sdk.bean.dto.response.events.payload;

import android.os.Parcel;
import android.os.Parcelable;
import com.alorma.github.sdk.bean.dto.response.Release;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.bean.dto.response.User;

/**
 * Created by Bernat on 30/05/2015.
 */
public class ReleaseEventPayload extends GithubEventPayload implements Parcelable {

  public static final Creator<ReleaseEventPayload> CREATOR = new Creator<ReleaseEventPayload>() {
    public ReleaseEventPayload createFromParcel(Parcel source) {
      return new ReleaseEventPayload(source);
    }

    public ReleaseEventPayload[] newArray(int size) {
      return new ReleaseEventPayload[size];
    }
  };
  public String action;
  public Release release;
  public Repo repository;
  public User sender;

  public ReleaseEventPayload() {
  }

  protected ReleaseEventPayload(Parcel in) {
    super(in);
    this.action = in.readString();
    this.release = in.readParcelable(Release.class.getClassLoader());
    this.repository = in.readParcelable(Repo.class.getClassLoader());
    this.sender = in.readParcelable(User.class.getClassLoader());
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeString(this.action);
    dest.writeParcelable(this.release, 0);
    dest.writeParcelable(this.repository, 0);
    dest.writeParcelable(this.sender, 0);
  }
}
